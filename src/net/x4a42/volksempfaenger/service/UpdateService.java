package net.x4a42.volksempfaenger.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.data.PodcastCursor;
import net.x4a42.volksempfaenger.data.UpdateServiceHelper;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.net.FeedDownloader;
import net.x4a42.volksempfaenger.service.internal.DatabaseWriter;
import net.x4a42.volksempfaenger.service.internal.FeedFetcher;
import net.x4a42.volksempfaenger.service.internal.PodcastData;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;

public class UpdateService extends IntentService {

	private static final int AWAIT_TERMINATION_TIMEOUT = 16;
	private static final String[] PODCAST_PROJECTION = { Podcast._ID,
			Podcast.TITLE, Podcast.FEED, Podcast.HTTP_LAST_MODIFIED,
			Podcast.HTTP_EXPIRES, Podcast.HTTP_ETAG };

	public static final String EXTRA_FIRST_SYNC = "first_sync";

	private ExecutorService feedFetcherPool;
	private ExecutorService databaseWriterPool;
	private FeedDownloader feedDownloader;
	private UpdateServiceHelper updateHelper;

	public UpdateService() {
		super("UpdateService");
	}

	@Override
	public void onCreate() {
		super.onCreate();

		int availableProcessors = Runtime.getRuntime().availableProcessors();

		feedFetcherPool = Executors.newFixedThreadPool(availableProcessors);
		databaseWriterPool = Executors.newSingleThreadExecutor();
		feedDownloader = new FeedDownloader(this);
		updateHelper = new UpdateServiceHelper(this);

	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Uri podcastUri = intent.getData();
		boolean extraFirstSync = intent
				.getBooleanExtra(EXTRA_FIRST_SYNC, false);

		// TODO networking stuff

		PodcastCursor cursor;
		if (podcastUri != null) {

			// sync a single podcast
			cursor = new PodcastCursor(getContentResolver().query(podcastUri,
					PODCAST_PROJECTION, null, null, null));

		} else {

			// sync all podcasts
			cursor = new PodcastCursor(getContentResolver().query(
					VolksempfaengerContentProvider.PODCAST_URI,
					PODCAST_PROJECTION, null, null, null));

		}

		while (cursor.moveToNext()) {
			PodcastData podcast = new PodcastData();
			podcast.uri = cursor.getUri();
			podcast.id = cursor.getId();
			podcast.title = cursor.getTitle();
			podcast.feed = cursor.getFeed();
			podcast.cacheInfo = cursor.getCacheInformation();
			podcast.firstSync = extraFirstSync;
			podcast.forceUpdate = (podcastUri == null);
			feedFetcherPool.execute(new FeedFetcher(this, podcast));
		}

		cursor.close();
	}

	public void onFeedParsed(PodcastData podcast, Feed feed) {
		databaseWriterPool.execute(new DatabaseWriter(this, podcast, feed));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		shutdownPool(feedFetcherPool);
		shutdownPool(databaseWriterPool);
	}

	private void shutdownPool(ExecutorService pool) {
		try {
			pool.shutdown();
			pool.awaitTermination(AWAIT_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Log.i(this, "Exception", e);
		}
	}

	public FeedDownloader getFeedDownloader() {
		return feedDownloader;
	}

	public UpdateServiceHelper getUpdateHelper() {
		return updateHelper;
	}

}
