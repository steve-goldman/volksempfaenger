package net.x4a42.volksempfaenger.data;

import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.net.CacheInformation;
import net.x4a42.volksempfaenger.net.FeedDownloader;
import net.x4a42.volksempfaenger.net.LogoDownloader;
import net.x4a42.volksempfaenger.net.NetException;
import net.x4a42.volksempfaenger.service.UpdateService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class PodcastHelper {

	public static void addFeed(Context context, String url)
			throws NetException, FeedParserException, Error.DuplicateException,
			Error.InsertException {
		final FeedDownloader fd = new FeedDownloader(context);
		CacheInformation cacheInfo = new CacheInformation();
		final Feed feed = fd.fetchFeed(url, cacheInfo);
		final ContentValues values = new ContentValues();
		values.put(Podcast.TITLE, feed.title);
		values.put(Podcast.DESCRIPTION, feed.description);
		values.put(Podcast.FEED, url);
		values.put(Podcast.WEBSITE, feed.website);

		final Uri newPodcastUri = context.getContentResolver().insert(
				VolksempfaengerContentProvider.PODCAST_URI, values);

		PodcastHelper.updateCacheInformation(context, newPodcastUri, cacheInfo);
		final Intent updatePodcast = new Intent(context, UpdateService.class);
		updatePodcast.setData(newPodcastUri);
		updatePodcast.putExtra("first_sync", true);
		context.startService(updatePodcast);

		final String feedImage = feed.image;
		if (feedImage != null) {
			// Try to download podcast logo
			final LogoDownloader ld = new LogoDownloader(context);
			try {
				ld.fetchLogo(feedImage, ContentUris.parseId(newPodcastUri));
			} catch (Exception e) {
				// Who cares?
			}
		}
	}

	public static void updateCacheInformation(Context context, Uri podcast,
			CacheInformation cacheInfo) {
		ContentValues values = new ContentValues();
		if (cacheInfo.expires != 0) {
			values.put(Podcast.HTTP_EXPIRES, cacheInfo.expires);
		}
		if (cacheInfo.lastModified != 0) {
			values.put(Podcast.HTTP_LAST_MODIFIED, cacheInfo.lastModified);
		}
		if (cacheInfo.eTag != null) {
			values.put(Podcast.HTTP_ETAG, cacheInfo.eTag);
		}
		if (values.size() > 0) {
			context.getContentResolver().update(podcast, values, null, null);
		}
	}
}
