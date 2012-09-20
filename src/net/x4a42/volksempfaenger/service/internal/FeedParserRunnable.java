package net.x4a42.volksempfaenger.service.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedParser;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.receiver.BackgroundErrorReceiver;
import net.x4a42.volksempfaenger.service.UpdateService;
import android.content.Intent;

public class FeedParserRunnable extends UpdateRunnable {

	private static final String TAG = "UpdateService";

	private PodcastData podcast;
	private File feedFile;
	private long startTime;

	public FeedParserRunnable(UpdateState update, PodcastData podcast,
			File feedFile) {
		super(update);
		this.podcast = podcast;
		this.feedFile = feedFile;
	}

	@Override
	public void run() {

		startTime = System.currentTimeMillis();
		Log.i(TAG, "Started parsing \"" + podcast.title + "\" [id="
				+ podcast.id + "]");

		try {
			Reader reader = new BufferedReader(new FileReader(feedFile));
			Feed feed = FeedParser.parse(reader);
			feed.localId = podcast.id;
			feed.firstSync = podcast.firstSync;
			onSuccess(feed);
		} catch (FileNotFoundException e) {
			onError(e);
		} catch (FeedParserException e) {
			onError(e);
		} catch (IOException e) {
			onError(e);
		} finally {
			feedFile.delete();
		}

	}

	private void onSuccess(Feed feed) {

		try {
			getUpdate().getUpdateService().enqueueLogoDownloader(getUpdate(),
					podcast, feed);
			getUpdate().getDatabaseWriterQueue().put(feed);
		} catch (InterruptedException e) {
			Log.w(TAG, e);
		}

		long endTime = System.currentTimeMillis();
		Log.i(TAG, "Finished parsing \"" + podcast.title + "\" [id="
				+ podcast.id + "] (took " + (endTime - startTime) + "ms)");

		getUpdate().decrementRemainingFeedCounter();

	}

	private void onError(Exception e) {

		long endTime = System.currentTimeMillis();
		Log.e(TAG, "Failed parsing \"" + podcast.title + "\" [id=" + podcast.id
				+ "] (took " + (endTime - startTime) + "ms)", e);

		// remove the podcast from the database as this was the first attempt to
		// fetch it and it failed
		if (podcast.firstSync) {

			int errorMessage = R.string.unknown_error;

			if (e instanceof FileNotFoundException) {
				errorMessage = R.string.message_podcast_feed_file_not_found_exception;
			} else if (e instanceof FeedParserException) {
				errorMessage = R.string.message_podcast_feed_parsing_failed;
			} else if (e instanceof IOException) {
				errorMessage = R.string.message_podcast_feed_io_exception;
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
