package net.x4a42.volksempfaenger.service.internal;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.PodcastHelper;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.net.NetException;

public class FeedDownloaderRunnable extends UpdateRunnable {

	private PodcastData podcast;

	public FeedDownloaderRunnable(UpdateState update, PodcastData podcast) {
		super(update);
		this.podcast = podcast;
	}

	@Override
	public void run() {
		long timeStart = System.currentTimeMillis();

		if (podcast.forceUpdate) {
			podcast.cacheInfo.expires = 0;
		}

		try {

			Feed feed = getUpdate().getUpdateService().getFeedDownloader()
					.fetchFeed(podcast.feed, podcast.cacheInfo);
			PodcastHelper.updateCacheInformation(
					getUpdate().getUpdateService(), podcast.uri,
					podcast.cacheInfo);
			onSuccess(feed);

		} catch (NetException e) {

			Log.w(this, e);
			onError();

		} catch (FeedParserException e) {

			Log.w(this, e);
			onError();

		}

		long timeEnd = System.currentTimeMillis();
		Log.v(this, "Updated " + podcast.title + " (took "
				+ (timeEnd - timeStart) + "ms)");
	}

	private void onSuccess(Feed feed) {
		// TODO
	}

	private void onError() {
		// TODO
	}

}
