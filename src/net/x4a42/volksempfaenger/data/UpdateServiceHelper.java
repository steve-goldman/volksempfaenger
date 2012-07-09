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
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class UpdateServiceHelper {

	private static final String EPISODE_WHERE = Episode.PODCAST_ID + "=?";
	private static final String EPISODE_WHERE_ITEM_ID = EPISODE_WHERE + " AND "
			+ Episode.FEED_ITEM_ID + "=?";
	private static final String ENCLOSURE_WHERE = Enclosure.EPISODE_ID
			+ "=? AND " + Enclosure.URL + "=?";
	private static final String[] HASH_QUERY_COLUMNS = { Episode.FEED_ITEM_ID,
			Episode.HASH };

	private DatabaseHelper dbHelper;
	@Deprecated
	private ContentResolver resolver;

	public UpdateServiceHelper(Context context) {
		dbHelper = DatabaseHelper.getInstance(context);
		resolver = context.getContentResolver();
	}

	public void updatePodcastFromFeed(long podcastId, Feed feed) {
		updatePodcastFromFeed(podcastId, feed, false);
	}

	public void updatePodcastFromFeed(long podcastId, Feed feed,
			boolean firstSync) {
		Log.d(this, "Updating podcast " + podcastId + " from " + feed);

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// fetch all hashes from the database to detect changes
		Map<String, String> episodeHashMap;
		{
			EpisodeCursor c = new EpisodeCursor(db.query(
					DatabaseHelper.TABLE_EPISODE, HASH_QUERY_COLUMNS,
					EPISODE_WHERE, new String[] { String.valueOf(podcastId) },
					null, null, null));
			episodeHashMap = new HashMap<String, String>(c.getCount());
			while (c.moveToNext()) {
				episodeHashMap.put(c.getFeeddItemId(), c.getHash());
			}
		}

		ContentValues values = new ContentValues();
		Uri latestEpisodeUri = null;
		Date latestEpisodeDate = null;

		for (FeedItem item : feed.items) {
			values.clear();
			values.put(Episode.PODCAST_ID, podcastId);
			values.put(Episode.FEED_ITEM_ID, item.itemId);
			values.put(Episode.TITLE, item.title);
			values.put(Episode.DATE, Utils.toUnixTimestamp(item.date));
			values.put(Episode.URL, item.url);
			values.put(Episode.DESCRIPTION, item.description);

			String newHash = Utils.hashContentValues(values);
			String oldHash = episodeHashMap.get(item.itemId);
			if (newHash.equals(oldHash)) {
				Log.d(this, "Skipping already existing item: " + item.title);
				continue;
			}

			values.put(Episode.HASH, newHash);
			if (firstSync) {
				values.put(Episode.STATUS, Constants.EPISODE_STATE_LISTENED);
			}

			Uri episodeUri;
			long episodeId;
			try {
				episodeUri = resolver.insert(
						VolksempfaengerContentProvider.EPISODE_URI, values);
				episodeId = ContentUris.parseId(episodeUri);
			} catch (Error.DuplicateException e) {
				Cursor c = resolver
						.query(VolksempfaengerContentProvider.EPISODE_URI,
								new String[] { Episode._ID },
								EPISODE_WHERE_ITEM_ID,
								new String[] { String.valueOf(podcastId),
										item.itemId }, null);
				if (!c.moveToFirst()) {
					// this should never happen actually
					continue;
				}
				episodeId = c.getLong(c.getColumnIndex(Episode._ID));
				c.close();
				episodeUri = ContentUris.withAppendedId(
						VolksempfaengerContentProvider.EPISODE_URI, episodeId);
				resolver.update(episodeUri, values, null, null);
			} catch (Error.InsertException e) {
				Log.wtf(this, e);
				return;
			}

			if (firstSync) {
				if (latestEpisodeDate == null
						|| latestEpisodeDate.before(item.date)) {
					latestEpisodeDate = item.date;
					latestEpisodeUri = episodeUri;
				}
			}

			long mainEnclosureId = 0;

			for (net.x4a42.volksempfaenger.feedparser.Enclosure enclosure : item.enclosures) {
				values.clear();
				values.put(Enclosure.EPISODE_ID, episodeId);
				values.put(Enclosure.TITLE, enclosure.title);
				values.put(Enclosure.URL, enclosure.url);
				values.put(Enclosure.MIME, enclosure.mime);
				values.put(Enclosure.SIZE, enclosure.size);

				Uri enclosureUri;
				long enclosureId;
				try {
					enclosureUri = resolver.insert(
							VolksempfaengerContentProvider.ENCLOSURE_URI,
							values);
					enclosureId = ContentUris.parseId(enclosureUri);
				} catch (Error.DuplicateException e) {
					Cursor c = resolver.query(
							VolksempfaengerContentProvider.ENCLOSURE_URI,
							new String[] { Enclosure._ID }, ENCLOSURE_WHERE,
							new String[] { String.valueOf(episodeId),
									enclosure.url }, null);
					if (!c.moveToFirst()) {
						// this should never happen actually
						continue;
					}
					enclosureId = c.getLong(c.getColumnIndex(Enclosure._ID));
					c.close();
					enclosureUri = ContentUris.withAppendedId(
							VolksempfaengerContentProvider.ENCLOSURE_URI,
							enclosureId);
					resolver.update(enclosureUri, values, null, null);
				} catch (Error.InsertException e) {
					Log.wtf(this, e);
					return;
				}

				// Try to find the main enclosure that will get downloaded
				if (item.enclosures.size() == 1) {
					// This is the only enclosure so it's the main one
					mainEnclosureId = enclosureId;
				} else {
					// TODO: try to automatically choose the best match
				}
			}

			// save mainEnclosureId in database
			if (mainEnclosureId > 0) {
				values.clear();
				values.put(Episode.ENCLOSURE_ID, mainEnclosureId);
				resolver.update(episodeUri, values, null, null);
			}

		}

		if (firstSync) {
			values.clear();
			values.put(Episode.STATUS, Constants.EPISODE_STATE_NEW);
			resolver.update(latestEpisodeUri, values, null, null);
		}

	}

}
