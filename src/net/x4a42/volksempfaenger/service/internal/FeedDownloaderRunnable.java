package net.x4a42.volksempfaenger.service.internal;

import java.io.File;
import java.io.IOException;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.PodcastHelper;
import net.x4a42.volksempfaenger.misc.StorageException;
import net.x4a42.volksempfaenger.net.NetException;
import net.x4a42.volksempfaenger.service.UpdateService;

public class FeedDownloaderRunnable extends UpdateRunnable {

	private static final String TEMP_FILE_PREFIX = "feed_";

	private PodcastData podcast;

	public FeedDownloaderRunnable(UpdateState update, PodcastData podcast) {
		super(update);
		this.podcast = podcast;
	}

	@Override
	public void run() {

		Log.v(this, "Started downloading " + podcast.toString() + " @ "
				+ System.currentTimeMillis());

		UpdateService service = getUpdate().getUpdateService();

		try {
			File target = File.createTempFile(TEMP_FILE_PREFIX, null,
					service.getCacheDir());
			service.getFeedDownloader().fetchFeed(podcast.feed,
					podcast.forceUpdate ? null : podcast.cacheInfo, target);
			PodcastHelper.updateCacheInformation(
					getUpdate().getUpdateService(), podcast.uri,
					podcast.cacheInfo);
			onSuccess(target);
		} catch (IOException e) {
			onError(e);
		} catch (NetException e) {
			onError(e);
		} catch (StorageException e) {
			onError(e);
		} finally {
			Log.v(this, "Finished downloading " + podcast.toString() + " @ "
					+ System.currentTimeMillis());
		}

	}

	private void onSuccess(File feed) {
		getUpdate().getUpdateService().enqueueFeedParser(getUpdate(), podcast,
				feed);
	}

	private void onError(Exception e) {
		// TODO
		Log.e(this, "Exception", e);
	}

}
