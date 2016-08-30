package net.x4a42.volksempfaenger.service.internal;

import java.io.File;
import java.io.IOException;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.PodcastHelper;
import net.x4a42.volksempfaenger.misc.StorageException;
import net.x4a42.volksempfaenger.net.FileDownloader.Result;
import net.x4a42.volksempfaenger.net.NetException;
import net.x4a42.volksempfaenger.receiver.BackgroundErrorReceiver;
import net.x4a42.volksempfaenger.service.UpdateService;
import android.content.Intent;

public class FeedDownloaderRunnable extends UpdateRunnable {

	private static final String TAG = "UpdateService";

	public static final String TEMP_FILE_PREFIX = "feed_";
	public static final String TEMP_FILE_SUFFIX = ".tmp";

	private PodcastData podcast;
	private long startTime;

	public FeedDownloaderRunnable(UpdateState update, PodcastData podcast) {
		super(update);
		this.podcast = podcast;
	}

	@Override
	public void run() {

		startTime = System.currentTimeMillis();
		Log.i(TAG, "Started downloading \"" + podcast.title + "\" [id="
				+ podcast.id + "]");

		UpdateService service = getUpdate().getUpdateService();

		try {
			File target = File.createTempFile(TEMP_FILE_PREFIX,
					TEMP_FILE_SUFFIX, service.getCacheDir());
			Result result = service.getFeedDownloader().fetchFeed(podcast.feed,
					podcast.forceUpdate ? null : podcast.cacheInfo, target);
			if (result == Result.CACHED) {
				target.delete();
				onCached();
			} else {
				// TODO move to database writer thread
				PodcastHelper.updateCacheInformation(getUpdate()
						.getUpdateService(), podcast.uri, podcast.cacheInfo);
				onSuccess(target);
			}
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

		long endTime = System.currentTimeMillis();
		Log.i(TAG, "Finished downloading \"" + podcast.title + "\" [id="
				+ podcast.id + "] (took " + (endTime - startTime) + "ms)");

		getUpdate().getUpdateService().enqueueFeedParser(getUpdate(), podcast,
				feed);

	}

	private void onCached() {

		long endTime = System.currentTimeMillis();
		Log.i(TAG, "Finished downloading \"" + podcast.title + "\" [id="
				+ podcast.id + "]; local version still valid (took "
				+ (endTime - startTime) + "ms)");

		getUpdate().decrementRemainingFeedCounter();

	}

	private void onError(Exception e) {

		long endTime = System.currentTimeMillis();
		Log.e(TAG, "Failed downloading \"" + podcast.title + "\" [id="
				+ podcast.id + "] (took " + (endTime - startTime) + "ms)", e);

		// remove the podcast from the database as this was the first attempt to
		// fetch it and it failed
		if (podcast.firstSync) {

			int errorMessage = R.string.unknown_error;

			if (e instanceof IOException) {
				errorMessage = R.string.message_podcast_feed_io_exception;
			} else if (e instanceof NetException) {
				errorMessage = R.string.message_podcast_feed_download_failed;
			} else if (e instanceof StorageException) {
				errorMessage = R.string.message_podcast_feed_storage_exception;
			}

			UpdateService updateService = getUpdate().getUpdateService();
			Intent intent = BackgroundErrorReceiver.getBackgroundErrorIntent(
					updateService.getString(R.string.dialog_error_title),
					updateService.getString(errorMessage),
					BackgroundErrorReceiver.ERROR_ADD);
			updateService.sendOrderedBroadcast(intent, null);
			getUpdate().getUpdateService().getContentResolver()
					.delete(podcast.uri, null, null);
		}

		getUpdate().decrementRemainingFeedCounter();

	}

}
