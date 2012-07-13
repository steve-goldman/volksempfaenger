package net.x4a42.volksempfaenger.data;

import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
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
		final Feed feed = fd.fetchFeed(url);
		final ContentValues values = new ContentValues();
		values.put(Podcast.TITLE, feed.title);
		values.put(Podcast.DESCRIPTION, feed.description);
		values.put(Podcast.FEED, url);
		values.put(Podcast.WEBSITE, feed.website);

		final Uri newPodcastUri = context.getContentResolver().insert(
				VolksempfaengerContentProvider.PODCAST_URI, values);

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
}
