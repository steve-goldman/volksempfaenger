package net.x4a42.volksempfaenger.service.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedParser;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;

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

		getUpdate().decrementRemainingFeedCounter();

	}

}
