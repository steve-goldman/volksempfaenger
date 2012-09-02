package net.x4a42.volksempfaenger.service.internal;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.feedparser.Feed;

public class LogoDownloaderRunnable extends UpdateRunnable {

	private static final String TAG = "UpdateService";

	private PodcastData podcast;
	private Feed feed;

	public LogoDownloaderRunnable(UpdateState update, PodcastData podcast,
			Feed feed) {
		super(update);
		this.podcast = podcast;
		this.feed = feed;
	}

	@Override
	public void run() {

		long startTime = System.currentTimeMillis();
		Log.i(TAG, "Started downloading logo of \"" + podcast.title + "\" [id="
				+ podcast.id + "]");

		// TODO

		long endTime = System.currentTimeMillis();
		Log.i(TAG, "Finished downloading logo of \"" + podcast.title
				+ "\" [id=" + podcast.id + "] (took " + (endTime - startTime)
				+ "ms)");

	}

}
