package net.x4a42.volksempfaenger.service.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.BlockingQueue;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.feedparser.Enclosure;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedItem;
import net.x4a42.volksempfaenger.feedparser.FeedParser;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.feedparser.FeedParserListener;

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
			FeedParser.parseEvented(reader, feedParserListener);
			onSuccess();
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

	private final FeedParserListener feedParserListener = new FeedParserListener() {

		private BlockingQueue<Object> databaseWriterQueue = getUpdate()
				.getDatabaseWriterQueue();

		private void enqueue(Object o) {
			try {
				databaseWriterQueue.put(o);
			} catch (InterruptedException e) {
				Log.w(this, e);
			}
		}

		@Override
		public void onFeed(Feed feed) {
			enqueue(feed);
		}

		@Override
		public void onFeedItem(FeedItem feedItem) {
			enqueue(feedItem);
		}

		@Override
		public void onEnclosure(Enclosure enclosure) {
			enqueue(enclosure);
		}

	};

	private void onSuccess() {

		long endTime = System.currentTimeMillis();
		Log.i(TAG, "Finished parsind \"" + podcast.title + "\" [id="
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
