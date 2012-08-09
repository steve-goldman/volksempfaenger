package net.x4a42.volksempfaenger.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns.Enclosure;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedItem;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

@Deprecated
public class LegacyUpdateServiceHelper {

	private static final String EPISODE_WHERE_ID = Episode._ID + "=?";
	private static final String EPISODE_WHERE_PODCAST_ID = Episode.PODCAST_ID
			+ "=?";
	private static final String EPISODE_WHERE_ITEM_ID = EPISODE_WHERE_PODCAST_ID
			+ " AND " + Episode.FEED_ITEM_ID + "=?";
	private static final String ENCLOSURE_WHERE = Enclosure.EPISODE_ID
			+ "=? AND " + Enclosure.URL + "=?";
	private static final String ENCLOSURE_WHERE_ID = Enclosure._ID + "=?";
	private static final String[] COLUMNS_HASH_QUERY = { Episode.FEED_ITEM_ID,
			Episode.HASH };
	private static final String[] COLUMNS_EPISODE_ID = { Episode._ID };
	private static final String[] COLUMNS_ENCLOSURE_ID = { Enclosure._ID };

	private DatabaseHelper dbHelper;
	private ContentResolver resolver;

	public LegacyUpdateServiceHelper(Context context) {
		dbHelper = DatabaseHelper.getInstance(context);
		resolver = context.getContentResolver();
	}

	public void updatePodcastFromFeed(long podcastId, Feed feed) {
		updatePodcastFromFeed(podcastId, feed, false);
	}

	public void updatePodcastFromFeed(long podcastId, Feed feed,
			boolean firstSync) {
		Log.v(this, "Updating podcast " + podcastId + " from " + feed);

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// fetch all hashes from the database to detect changes
		Map<String, String> episodeHashMap;
		{
			EpisodeCursor c = new EpisodeCursor(db.query(
					DatabaseHelper.TABLE_EPISODE, COLUMNS_HASH_QUERY,
					EPISODE_WHERE_PODCAST_ID,
					new String[] { String.valueOf(podcastId) }, null, null,
					null));
			episodeHashMap = new HashMap<String, String>(c.getCount());
			while (c.moveToNext()) {
				episodeHashMap.put(c.getFeeddItemId(), c.getHash());
			}
			c.close();
		}

		db.beginTransaction();

		ContentValues values = new ContentValues();
		long latestEpisodeId = -1;
		Date latestEpisodeDate = null;

		for (FeedItem item : feed.items) {
			values.clear();
			values.put(Episode.PODCAST_ID, podcastId);
			values.put(Episode.FEED_ITEM_ID, item.itemId);
			values.put(Episode.TITLE, item.title);
			values.put(Episode.DATE, Utils.toUnixTimestamp(item.date));
			values.put(Episode.URL, item.url);
			values.put(Episode.DESCRIPTION, item.description);
			values.put(Episode.FLATTR_URL, item.flattrUrl);
			int flattrStatus;
			if (item.flattrUrl == null) {
				flattrStatus = Constants.FLATTR_STATE_NONE;
			} else {
				flattrStatus = Constants.FLATTR_STATE_NEW;
			}
			values.put(Episode.FLATTR_STATUS, flattrStatus);

			String newHash = Utils.hashContentValues(values);
			String oldHash = episodeHashMap.get(item.itemId);
			if (newHash.equals(oldHash)) {
				Log.v(this, "Skipping already existing item: " + item.title);
				continue;
			}

			values.put(Episode.HASH, newHash);
			if (firstSync) {
				values.put(Episode.STATUS, Constants.EPISODE_STATE_LISTENED);
			}

			long episodeId;

			try {

				episodeId = db.insertOrThrow(DatabaseHelper.TABLE_EPISODE,
						null, values);

			} catch (SQLiteConstraintException e) {
				values.remove(Episode.FLATTR_STATUS);
				Cursor c = db
						.query(DatabaseHelper.TABLE_EPISODE,
								COLUMNS_EPISODE_ID, EPISODE_WHERE_ITEM_ID,
								new String[] { String.valueOf(podcastId),
										item.itemId }, null, null, null);

				if (!c.moveToFirst()) {
					// this should never happen actually
					Log.v(this,
							"Got SQLiteConstraintException but could not find conflicting row",
							e);
					continue;
				}

				// get id of conflicting episode
				episodeId = c.getLong(c.getColumnIndex(Episode._ID));
				c.close();
				// update conflicting episode
				db.update(DatabaseHelper.TABLE_EPISODE, values,
						EPISODE_WHERE_ID,
						new String[] { String.valueOf(episodeId) });

			} catch (Error.InsertException e) {

				Log.wtf(this, e);
				return;

			} finally {

				db.yieldIfContendedSafely();

			}

			if (firstSync
					&& (latestEpisodeDate == null || latestEpisodeDate
							.before(item.date))) {
				latestEpisodeDate = item.date;
				latestEpisodeId = episodeId;
			}

			long mainEnclosureId = -1;

			for (net.x4a42.volksempfaenger.feedparser.Enclosure enclosure : item.enclosures) {
				values.clear();
				values.put(Enclosure.EPISODE_ID, episodeId);
				values.put(Enclosure.TITLE, enclosure.title);
				values.put(Enclosure.URL, enclosure.url);
				values.put(Enclosure.MIME, enclosure.mime);
				values.put(Enclosure.SIZE, enclosure.size);

				long enclosureId;

				try {

					enclosureId = db.insert(DatabaseHelper.TABLE_ENCLOSURE,
							null, values);

				} catch (Error.DuplicateException e) {

					Cursor c = db.query(DatabaseHelper.TABLE_ENCLOSURE,
							COLUMNS_ENCLOSURE_ID, ENCLOSURE_WHERE,
							new String[] { String.valueOf(episodeId),
									enclosure.url }, null, null, null);

					if (!c.moveToFirst()) {
						// this should never happen actually
						Log.wtf(this,
								"Got SQLiteConstraintException but could not find conflicting row",
								e);
						continue;
					}

					// get id of conflicting enclosure
					enclosureId = c.getLong(c.getColumnIndex(Enclosure._ID));
					c.close();
					// update conflicting enclosure
					db.update(DatabaseHelper.TABLE_ENCLOSURE, values,
							ENCLOSURE_WHERE_ID,
							new String[] { String.valueOf(enclosureId) });

				} catch (Error.InsertException e) {

					Log.wtf(this, e);
					return;

				} finally {

					db.yieldIfContendedSafely();

				}

				if (item.enclosures.size() == 1) {
					mainEnclosureId = enclosureId;
				}

			}

			// save mainEnclosureId in database
			if (mainEnclosureId > 0) {
				values.clear();
				values.put(Episode.ENCLOSURE_ID, mainEnclosureId);
				db.update(DatabaseHelper.TABLE_EPISODE, values,
						EPISODE_WHERE_ID,
						new String[] { String.valueOf(episodeId) });
			}

		}

		if (firstSync) {
			values.clear();
			values.put(Episode.STATUS, Constants.EPISODE_STATE_NEW);
			db.update(DatabaseHelper.TABLE_EPISODE, values, EPISODE_WHERE_ID,
					new String[] { String.valueOf(latestEpisodeId) });
		}

		db.setTransactionSuccessful();
		db.endTransaction();

		resolver.notifyChange(VolksempfaengerContentProvider.CONTENT_URI, null);

	}

}
