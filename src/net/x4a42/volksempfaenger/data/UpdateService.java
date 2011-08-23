package net.x4a42.volksempfaenger.data;

import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.feedparser.Enclosure;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedItem;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.net.FeedDownloader;
import net.x4a42.volksempfaenger.net.NetException;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class UpdateService extends Service {

	private static long lastRun = 0;

	private DbHelper dbHelper;

	private class UpdateTask extends AsyncTask<Long, Long, Void> {

		private int podcastCount;

		@Override
		protected Void doInBackground(Long... params) {

			Cursor cursor;

			if (params == null) {
				// All podcasts get updated as no IDs were passed
				lastRun = System.currentTimeMillis();

				cursor = dbHelper.getReadableDatabase().query(
						DbHelper.Podcast._TABLE, null, null, null, null, null,
						null);
			} else {
				cursor = dbHelper.getReadableDatabase().query(
						DbHelper.Podcast._TABLE,
						null,
						String.format("%s in (%s)", DbHelper.Podcast.ID,
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
						.getColumnIndex(DbHelper.Podcast.ID));

				String podcastUrl = cursor.getString(cursor
						.getColumnIndex(DbHelper.Podcast.URL));

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
					values.put(DbHelper.Episode.PODCAST, podcastId);
					values.put(DbHelper.Episode.ITEM_ID, item.getItemId());
					values.put(DbHelper.Episode.TITLE, item.getTitle());
					values.put(DbHelper.Episode.DATE,
							Utils.toUnixTimestamp(item.getDate()));
					values.put(DbHelper.Episode.URL, item.getUrl());
					values.put(DbHelper.Episode.DESCRIPTION,
							item.getDescription());

					long itemId;
					try {
						itemId = db.insertOrThrow(DbHelper.Episode._TABLE,
								null, values);
					} catch (SQLException e) {
						if (e instanceof SQLiteConstraintException) {
							Cursor c = db.query(DbHelper.Episode._TABLE,
									new String[] { DbHelper.Episode.ID },
									String.format("%s = ? AND %s = ?",
											DbHelper.Episode.PODCAST,
											DbHelper.Episode.ITEM_ID),
									new String[] { String.valueOf(podcastId),
											item.getItemId() }, null, null,
									null);
							if (!c.moveToFirst()) {
								continue;
							}
							itemId = c.getLong(c
									.getColumnIndex(DbHelper.Episode.ID));
							db.update(DbHelper.Episode._TABLE, values, String
									.format("%s = ?", DbHelper.Episode.ID),
									new String[] { String.valueOf(itemId) });
						} else {
							Log.wtf(getClass().getSimpleName(), e);
							continue;
						}
					}

					for (Enclosure enclosure : item.getEnclosures()) {
						values.clear();
						values.put(DbHelper.Enclosure.EPISODE, itemId);
						values.put(DbHelper.Enclosure.TITLE,
								enclosure.getTitle());
						values.put(DbHelper.Enclosure.URL, enclosure.getUrl());
						values.put(DbHelper.Enclosure.MIME, enclosure.getMime());
						values.put(DbHelper.Enclosure.SIZE, enclosure.getSize());

						long enclosureId;
						try {
							enclosureId = db.insertOrThrow(
									DbHelper.Enclosure._TABLE, null, values);
						} catch (SQLException e) {
							if (e instanceof SQLiteConstraintException) {
								Cursor c = db.query(DbHelper.Enclosure._TABLE,
										new String[] { DbHelper.Enclosure.ID },
										String.format("%s = ? AND %s = ?",
												DbHelper.Enclosure.EPISODE,
												DbHelper.Enclosure.URL),
										new String[] { String.valueOf(itemId),
												enclosure.getUrl() }, null,
										null, null);
								if (!c.moveToFirst()) {
									continue;
								}
								enclosureId = c.getLong(c
										.getColumnIndex(DbHelper.Enclosure.ID));
								db.update(DbHelper.Enclosure._TABLE, values,
										String.format("%s = ?",
												DbHelper.Enclosure.ID),
										new String[] { String
												.valueOf(enclosureId) });
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
		dbHelper = new DbHelper(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Long[] id = null;
		Bundle extras = intent.getExtras();
		if (extras != null) {
			long[] extraId = extras.getLongArray("id");
			if (extraId.length != 0) {
				id = new Long[extraId.length];
				for (int i = 0; i < id.length; i++) {
					id[i] = extraId[i];
				}
			}
		}

		new UpdateTask().execute(id);

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