package net.x4a42.volksempfaenger.service;

import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.DatabaseHelper;
import net.x4a42.volksempfaenger.data.DatabaseHelper.Episode;
import net.x4a42.volksempfaenger.data.DatabaseHelper.Podcast;
import net.x4a42.volksempfaenger.feedparser.Enclosure;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedItem;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.net.FeedDownloader;
import net.x4a42.volksempfaenger.net.NetException;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class UpdateService extends Service {

	private static long lastRun = 0;

	private DatabaseHelper dbHelper;

	private class UpdateTask extends AsyncTask<Long, Long, Void> {

		private int podcastCount;

		@Override
		protected Void doInBackground(Long... params) {

			Cursor cursor;

			if (params == null) {
				// All podcasts get updated as no IDs were passed
				lastRun = System.currentTimeMillis();

				cursor = dbHelper.getReadableDatabase().query(
						DatabaseHelper.Podcast._TABLE, null, null, null, null,
						null, null);
			} else {
				cursor = dbHelper.getReadableDatabase().query(
						DatabaseHelper.Podcast._TABLE,
						null,
						String.format("%s in (%s)", DatabaseHelper.Podcast.ID,
								Utils.joinArray(params, ",")), null, null,
						null, null);
			}

			podcastCount = cursor.getCount();

			if (podcastCount == 0) {
				return null;
			}

			FeedDownloader feedDownloader = new FeedDownloader(
					UpdateService.this);

			while (cursor.moveToNext()) {
				long podcastId = cursor.getLong(cursor
						.getColumnIndex(DatabaseHelper.Podcast.ID));

				String podcastUrl = cursor.getString(cursor
						.getColumnIndex(DatabaseHelper.Podcast.URL));

				Feed feed = null;

				try {
					feed = feedDownloader.fetchFeed(podcastUrl);
				} catch (NetException e) {
					// TODO Auto-generated catch block
					Log.w(getClass().getSimpleName(), e);
					continue;
				} catch (FeedParserException e) {
					// TODO Auto-generated catch block
					Log.w(getClass().getSimpleName(), e);
					continue;
				}

				ContentValues values = new ContentValues();
				SQLiteDatabase db = dbHelper.getWritableDatabase();

				for (FeedItem item : feed.getItems()) {
					values.clear();
					values.put(DatabaseHelper.Episode.PODCAST, podcastId);
					values.put(DatabaseHelper.Episode.ITEM_ID, item.getItemId());
					values.put(DatabaseHelper.Episode.TITLE, item.getTitle());
					values.put(DatabaseHelper.Episode.DATE,
							Utils.toUnixTimestamp(item.getDate()));
					values.put(DatabaseHelper.Episode.URL, item.getUrl());
					values.put(DatabaseHelper.Episode.DESCRIPTION,
							item.getDescription());

					long itemId;
					try {
						itemId = db.insertOrThrow(
								DatabaseHelper.Episode._TABLE, null, values);
					} catch (SQLException e) {
						if (e instanceof SQLiteConstraintException) {
							Cursor c = db.query(DatabaseHelper.Episode._TABLE,
									new String[] { DatabaseHelper.Episode.ID },
									String.format("%s = ? AND %s = ?",
											DatabaseHelper.Episode.PODCAST,
											DatabaseHelper.Episode.ITEM_ID),
									new String[] { String.valueOf(podcastId),
											item.getItemId() }, null, null,
									null);
							if (!c.moveToFirst()) {
								continue;
							}
							itemId = c.getLong(c
									.getColumnIndex(DatabaseHelper.Episode.ID));
							db.update(DatabaseHelper.Episode._TABLE, values,
									String.format("%s = ?",
											DatabaseHelper.Episode.ID),
									new String[] { String.valueOf(itemId) });
						} else {
							Log.wtf(getClass().getSimpleName(), e);
							continue;
						}
					}

					for (Enclosure enclosure : item.getEnclosures()) {
						values.clear();
						values.put(DatabaseHelper.Enclosure.EPISODE, itemId);
						values.put(DatabaseHelper.Enclosure.TITLE,
								enclosure.getTitle());
						values.put(DatabaseHelper.Enclosure.URL,
								enclosure.getUrl());
						values.put(DatabaseHelper.Enclosure.MIME,
								enclosure.getMime());
						values.put(DatabaseHelper.Enclosure.SIZE,
								enclosure.getSize());

						try {
							db.insertOrThrow(DatabaseHelper.Enclosure._TABLE,
									null, values);
						} catch (SQLException e) {
							if (e instanceof SQLiteConstraintException) {
								Cursor c = db
										.query(DatabaseHelper.Enclosure._TABLE,
												new String[] { DatabaseHelper.Enclosure.ID },
												String.format(
														"%s = ? AND %s = ?",
														DatabaseHelper.Enclosure.EPISODE,
														DatabaseHelper.Enclosure.URL),
												new String[] {
														String.valueOf(itemId),
														enclosure.getUrl() },
												null, null, null);
								if (!c.moveToFirst()) {
									continue;
								}
								long enclosureId = c
										.getLong(c
												.getColumnIndex(DatabaseHelper.Enclosure.ID));
								String filename = Utils
										.filenameFromUrl(enclosure.getUrl());
								if (!Utils.getEnclosureFile(UpdateService.this,
										enclosureId, filename).isFile()) {
									db.update(
											DatabaseHelper.Enclosure._TABLE,
											values,
											String.format("%s = ?",
													DatabaseHelper.Enclosure.ID),
											new String[] { String
													.valueOf(enclosureId) });
								}
							} else {
								Log.wtf(getClass().getSimpleName(), e);
								continue;
							}
						}
					}
				}

				publishProgress(podcastId);
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Long... values) {
			Toast.makeText(UpdateService.this, "Updated " + values[0],
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPostExecute(Void result) {
			stopSelf();
		}

	}

	@Override
	public void onCreate() {
		dbHelper = new DatabaseHelper(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Long[] id = null;
		long[] extraId = intent.getLongArrayExtra("id");
		if (extraId != null && extraId.length > 0) {
			if (extraId.length != 0) {
				id = new Long[extraId.length];
				for (int i = 0; i < id.length; i++) {
					id[i] = extraId[i];
				}
			}
		}

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		if (id == null && !cm.getBackgroundDataSetting()) {
			// background data is disabled
			stopSelf();
		} else {
			// start UpdateTask
			new UpdateTask().execute(id);
		}

		return START_NOT_STICKY;

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		dbHelper.close();
	}

	public static long getLastRun() {
		return lastRun;
	}

}