package net.x4a42.volksempfaenger.service;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.data.PodcastCursor;
import net.x4a42.volksempfaenger.data.PodcastHelper;
import net.x4a42.volksempfaenger.data.UpdateServiceHelper;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.net.CacheInformation;
import net.x4a42.volksempfaenger.net.LegacyFeedDownloader;
import net.x4a42.volksempfaenger.net.NetException;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

@Deprecated
public class LegacyUpdateService extends IntentService {

	private static long lastRun = 0;

	public LegacyUpdateService() {
		super(LegacyUpdateService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Uri podcast = intent.getData();
		boolean extraFirstSync = intent.getBooleanExtra("first_sync", false);

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (podcast == null
				&& (netInfo == null || netInfo.getState() == NetworkInfo.State.DISCONNECTED)) {
			// If podcast == null, we sync all podcasts in the background. Thus
			// we just return if background data is disabled.
			return;
		}

		PodcastCursor cursor;
		{
			String[] projection = { Podcast._ID, Podcast.TITLE, Podcast.FEED,
					Podcast.HTTP_LAST_MODIFIED, Podcast.HTTP_EXPIRES,
					Podcast.HTTP_ETAG };

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

		LegacyFeedDownloader feedDownloader = new LegacyFeedDownloader(this);
		UpdateServiceHelper updateHelper = new UpdateServiceHelper(this);

		long timeStart, timeEnd, timeFeedStart, timeFeedEnd;
		timeStart = System.currentTimeMillis();

		LegacyUpdateServiceStatus.startUpdate();

		while (cursor.moveToNext()) {
			LegacyUpdateServiceStatus.startUpdate(cursor.getUri());

			Log.v(this, "Updating " + cursor.getTitle());

			timeFeedStart = System.currentTimeMillis();

			long podcastId = cursor.getId();

			String podcastFeed = cursor.getFeed();

			Feed feed = null;
			CacheInformation cacheInfo = cursor.getCacheInformation();
			if (podcast != null) {
				// if a single podcast is updated, any cache information should
				// be ignored, because currently there is no way to force an
				// update
				cacheInfo.expires = 0;
				cacheInfo.lastModified = 0;
				cacheInfo.eTag = null;
			}

			try {
				feed = feedDownloader.fetchFeed(podcastFeed, cacheInfo);
			} catch (NetException e) {
				// TODO Auto-generated catch block
				Log.w(this, e);
				LegacyUpdateServiceStatus.stopUpdate(cursor.getUri());
				continue;
			} catch (FeedParserException e) {
				// TODO Auto-generated catch block
				Log.w(this, e);
				LegacyUpdateServiceStatus.stopUpdate(cursor.getUri());
				continue;
			}
			PodcastHelper.updateCacheInformation(this, cursor.getUri(),
					cacheInfo);
			if (feed != null) {
				updateHelper.updatePodcastFromFeed(podcastId, feed,
						extraFirstSync);
				timeFeedEnd = System.currentTimeMillis();
				Log.v(this, "Updated " + feed.title + " (took "
						+ (timeFeedEnd - timeFeedStart) + "ms)");
			}

			LegacyUpdateServiceStatus.stopUpdate(cursor.getUri());
		}
		cursor.close();

		LegacyUpdateServiceStatus.stopUpdate();

		timeEnd = System.currentTimeMillis();
		Log.v(this, "Update took " + (timeEnd - timeStart) + "ms");

		// start DownloadService to start automatic downloads if enabled
		startService(new Intent(this, DownloadService.class));
	}

	public static long getLastRun() {
		return lastRun;
	}

}
