package net.x4a42.volksempfaenger.service;

import java.util.Date;

import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.DatabaseHelper;
import net.x4a42.volksempfaenger.feedparser.Enclosure;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedItem;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.net.FeedDownloader;
import net.x4a42.volksempfaenger.net.NetException;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.util.Log;

public class UpdateService extends IntentService {

	private static long lastRun = 0;

	private DatabaseHelper dbHelper;

	public UpdateService() {
		super(UpdateService.class.getSimpleName());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dbHelper = DatabaseHelper.getInstance(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		long[] extraId = intent.getLongArrayExtra("id");
		boolean extraFirstSync = intent.getBooleanExtra("first_sync", false);

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		if (extraId == null && !cm.getBackgroundDataSetting()) {
			// background data is disabled
			return;
		}

		Cursor cursor;

		if (extraId == null) {
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
							Utils.joinArray(extraId, ",")), null, null, null,
					null);
		}

		if (cursor.getCount() == 0) {
			return;
		}

		FeedDownloader feedDownloader = new FeedDownloader(UpdateService.this);

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

			long latestEpisodeId = 0;
			Date latestEpisodeDate = null;

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
				if (extraFirstSync) {
					// if this is the first sync, set all episodes to be
					// listened
					values.put(DatabaseHelper.Episode.STATE,
							DatabaseHelper.Episode.STATE_LISTENED);
				}

				long itemId;
				try {
					itemId = db.insertOrThrow(DatabaseHelper.Episode._TABLE,
							null, values);
				} catch (SQLException e) {
					if (e instanceof SQLiteConstraintException) {
						Cursor c = db.query(
								DatabaseHelper.Episode._TABLE,
								new String[] { DatabaseHelper.Episode.ID },
								String.format("%s = ? AND %s = ?",
										DatabaseHelper.Episode.PODCAST,
										DatabaseHelper.Episode.ITEM_ID),
								new String[] { String.valueOf(podcastId),
										item.getItemId() }, null, null, null);
						if (!c.moveToFirst()) {
							continue;
						}
						itemId = c.getLong(c
								.getColumnIndex(DatabaseHelper.Episode.ID));
						db.update(DatabaseHelper.Episode._TABLE, values, String
								.format("%s = ?", DatabaseHelper.Episode.ID),
								new String[] { String.valueOf(itemId) });
					} else {
						Log.wtf(getClass().getSimpleName(), e);
						continue;
					}
				}

				if (extraFirstSync) {
					if (latestEpisodeDate == null
							|| latestEpisodeDate.before(item.getDate())) {
						latestEpisodeDate = item.getDate();
						latestEpisodeId = itemId;
					}
				}

				long mainEnclosureId = 0;

				for (Enclosure enclosure : item.getEnclosures()) {
					values.clear();
					values.put(DatabaseHelper.Enclosure.EPISODE, itemId);
					values.put(DatabaseHelper.Enclosure.TITLE,
							enclosure.getTitle());
					values.put(DatabaseHelper.Enclosure.URL, enclosure.getUrl());
					values.put(DatabaseHelper.Enclosure.MIME,
							enclosure.getMime());
					values.put(DatabaseHelper.Enclosure.SIZE,
							enclosure.getSize());

					long enclosureId = 0;

					try {
						enclosureId = db.insertOrThrow(
								DatabaseHelper.Enclosure._TABLE, null, values);
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
													enclosure.getUrl() }, null,
											null, null);
							if (!c.moveToFirst()) {
								continue;
							}
							enclosureId = c
									.getLong(c
											.getColumnIndex(DatabaseHelper.Enclosure.ID));
							String filename = Utils.filenameFromUrl(enclosure
									.getUrl());
							if (!Utils.getEnclosureFile(UpdateService.this,
									enclosureId, filename).isFile()) {
								db.update(DatabaseHelper.Enclosure._TABLE,
										values, String.format("%s = ?",
												DatabaseHelper.Enclosure.ID),
										new String[] { String
												.valueOf(enclosureId) });
							}
						} else {
							Log.wtf(getClass().getSimpleName(), e);
							continue;
						}
					}
					// Try to find the main enclosure that will get downloaded
					if (item.getEnclosures().size() == 1) {
						// This is the only enclosure so it's the main one
						mainEnclosureId = enclosureId;
					} else {
						// TODO: try to automatically choose the best match
					}
				}
				// save mainEnclosureId in database
				if (mainEnclosureId > 0) {
					values.clear();
					values.put(DatabaseHelper.Episode.ENCLOSURE,
							mainEnclosureId);
					db.update(DatabaseHelper.Episode._TABLE, values,
							String.format("%s = ?", DatabaseHelper.Episode.ID),
							new String[] { String.valueOf(itemId) });
				}
			}

			if (extraFirstSync) {
				values.clear();
				values.put(DatabaseHelper.Episode.STATE,
						DatabaseHelper.Episode.STATE_NEW);
				db.update(DatabaseHelper.Episode._TABLE, values,
						String.format("%s = ?", DatabaseHelper.Episode.ID),
						new String[] { String.valueOf(latestEpisodeId) });
			}

			Log.d(getClass().getSimpleName(), "Updated " + feed.getTitle());
		}

		// start DownloadService to start automatic downloads if enabled
		Intent downloadServiceIntent = new Intent(this, DownloadService.class);
		startService(downloadServiceIntent);
	}

	public static long getLastRun() {
		return lastRun;
	}

}
