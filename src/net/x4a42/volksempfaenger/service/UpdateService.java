package net.x4a42.volksempfaenger.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns.Enclosure;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.Error;
import net.x4a42.volksempfaenger.data.PodcastCursor;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedItem;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.net.FeedDownloader;
import net.x4a42.volksempfaenger.net.NetException;
import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

public class UpdateService extends IntentService {

	private static final String EPISODE_WHERE = Episode.PODCAST_ID + "=?";
	private static final String EPISODE_WHERE_ITEM_ID = EPISODE_WHERE + " AND "
			+ Episode.FEED_ITEM_ID + "=?";
	private static final String ENCLOSURE_WHERE = Enclosure.EPISODE_ID
			+ "=? AND " + Enclosure.URL + "=?";
	private static long lastRun = 0;

	public UpdateService() {
		super(UpdateService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Uri podcast = intent.getData();
		boolean extraFirstSync = intent.getBooleanExtra("first_sync", false);

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (podcast == null
				&& netInfo.getState() == NetworkInfo.State.DISCONNECTED) {
			// If podcast == null, we sync all podcasts in the background. Thus
			// we just return if background data is disabled.
			return;
		}

		PodcastCursor cursor;
		{
			String[] projection = { Podcast._ID, Podcast.TITLE, Podcast.FEED };

			if (podcast != null) {
				// Sync a single podcast
				cursor = new PodcastCursor(getContentResolver().query(podcast,
						projection, null, null, null));
			} else {
				lastRun = System.currentTimeMillis();
				cursor = new PodcastCursor(getContentResolver().query(
						VolksempfaengerContentProvider.PODCAST_URI, projection,
						null, null, null));
			}
		}

		if (cursor.getCount() == 0) {
			// There are just no podcasts to update.
			cursor.close();
			return;
		}

		FeedDownloader feedDownloader = new FeedDownloader(this);

		long timeStart, timeEnd, timeFeedStart, timeFeedEnd;
		timeStart = System.currentTimeMillis();

		UpdateServiceStatus.lock();
		UpdateServiceStatus.startUpdate();

		while (cursor.moveToNext()) {
			UpdateServiceStatus.startUpdate(cursor.getUri());

			Log.d(this,
					"Updating "
							+ cursor.getString(cursor
									.getColumnIndex(Podcast.TITLE)));

			timeFeedStart = System.currentTimeMillis();

			long podcastId = cursor.getLong(cursor.getColumnIndex(Podcast._ID));

			String podcastFeed = cursor.getString(cursor
					.getColumnIndex(Podcast.FEED));

			Feed feed = null;

			try {
				feed = feedDownloader.fetchFeed(podcastFeed);
			} catch (NetException e) {
				// TODO Auto-generated catch block
				Log.w(this, e);
				continue;
			} catch (FeedParserException e) {
				// TODO Auto-generated catch block
				Log.w(this, e);
				continue;
			}

			ContentValues values = new ContentValues();

			// TODO: update podcast table

			Map<String, String> episodeHashMap;
			{
				EpisodeCursor c = new EpisodeCursor(getContentResolver().query(
						VolksempfaengerContentProvider.EPISODE_URI,
						new String[] { Episode._ID, Episode.FEED_ITEM_ID,
								Episode.HASH }, EPISODE_WHERE,
						new String[] { String.valueOf(podcastId) }, null));
				episodeHashMap = new HashMap<String, String>(c.getCount());
				while (c.moveToNext()) {
					episodeHashMap.put(c.getFeeddItemId(), c.getHash());
				}
			}

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
				if (extraFirstSync) {
					values.put(Episode.STATUS, Constants.EPISODE_STATE_LISTENED);
				}

				Uri episodeUri;
				long episodeId;
				try {
					episodeUri = getContentResolver().insert(
							VolksempfaengerContentProvider.EPISODE_URI, values);
					episodeId = ContentUris.parseId(episodeUri);
				} catch (Error.DuplicateException e) {
					Cursor c = getContentResolver().query(
							VolksempfaengerContentProvider.EPISODE_URI,
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
							VolksempfaengerContentProvider.EPISODE_URI,
							episodeId);
					getContentResolver().update(episodeUri, values, null, null);
				} catch (Error.InsertException e) {
					Log.wtf(this, e);
					return;
				}

				if (extraFirstSync) {
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
						enclosureUri = getContentResolver().insert(
								VolksempfaengerContentProvider.ENCLOSURE_URI,
								values);
						enclosureId = ContentUris.parseId(enclosureUri);
					} catch (Error.DuplicateException e) {
						Cursor c = getContentResolver().query(
								VolksempfaengerContentProvider.ENCLOSURE_URI,
								new String[] { Enclosure._ID },
								ENCLOSURE_WHERE,
								new String[] { String.valueOf(episodeId),
										enclosure.url }, null);
						if (!c.moveToFirst()) {
							// this should never happen actually
							continue;
						}
						enclosureId = c
								.getLong(c.getColumnIndex(Enclosure._ID));
						c.close();
						enclosureUri = ContentUris.withAppendedId(
								VolksempfaengerContentProvider.ENCLOSURE_URI,
								enclosureId);
						getContentResolver().update(enclosureUri, values, null,
								null);
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
					getContentResolver().update(episodeUri, values, null, null);
				}
			}

			if (extraFirstSync) {
				values.clear();
				values.put(Episode.STATUS, Constants.EPISODE_STATE_NEW);
				getContentResolver().update(latestEpisodeUri, values, null,
						null);
			}

			timeFeedEnd = System.currentTimeMillis();
			Log.d(this, "Updated " + feed.title + " (took "
					+ (timeFeedEnd - timeFeedStart) + "ms)");

			UpdateServiceStatus.stopUpdate(cursor.getUri());
		}
		cursor.close();

		UpdateServiceStatus.stopUpdate();
		UpdateServiceStatus.unlock();

		timeEnd = System.currentTimeMillis();
		Log.d(this, "Update took " + (timeEnd - timeStart) + "ms");

		// start DownloadService to start automatic downloads if enabled
		startService(new Intent(this, DownloadService.class));
	}

	public static long getLastRun() {
		return lastRun;
	}

}
