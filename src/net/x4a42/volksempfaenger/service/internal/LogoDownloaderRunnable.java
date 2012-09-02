package net.x4a42.volksempfaenger.service.internal;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.net.LogoDownloader;
import net.x4a42.volksempfaenger.service.UpdateService;

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

		UpdateService updateService = getUpdate().getUpdateService();

		if (feed != null
				&& feed.image != null
				&& !Utils.getPodcastLogoFile(updateService, podcast.id)
						.exists()) {

			LogoDownloader ld = new LogoDownloader(updateService);
			try {

				ld.fetchLogo(feed.image, podcast.id);

				long endTime = System.currentTimeMillis();
				Log.i(TAG, "Finished downloading logo of \"" + podcast.title
						+ "\" [id=" + podcast.id + "] (took "
						+ (endTime - startTime) + "ms)");

			} catch (Exception e) {

				long endTime = System.currentTimeMillis();
				Log.w(TAG, "Failed downloading logo of \"" + podcast.title
						+ "\" [id=" + podcast.id + "] (took "
						+ (endTime - startTime) + "ms)");

			}

		} else {

			// TODO update the logo
			long endTime = System.currentTimeMillis();
			Log.i(TAG, "Skipped downloading logo of \"" + podcast.title
					+ "\" [id=" + podcast.id + "] (took "
					+ (endTime - startTime) + "ms)");

		}

	}

}
