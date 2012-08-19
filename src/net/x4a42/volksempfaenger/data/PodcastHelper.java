package net.x4a42.volksempfaenger.data;

import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.net.CacheInformation;
import net.x4a42.volksempfaenger.net.NetException;
import net.x4a42.volksempfaenger.service.UpdateService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class PodcastHelper {

	public static void addFeed(Context context, String url)
			throws NetException, FeedParserException, Error.DuplicateException,
			Error.InsertException {

		ContentValues values = new ContentValues();
		values.put(Podcast.FEED, url);

		Uri newPodcastUri = context.getContentResolver().insert(
				VolksempfaengerContentProvider.PODCAST_URI, values);

		Intent updatePodcast = new Intent(context, UpdateService.class);
		updatePodcast.setData(newPodcastUri);
		context.startService(updatePodcast);

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
