package net.x4a42.volksempfaenger.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
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
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

// TODO
public class UpdateService extends Service {

	private static final int AWAIT_TERMINATION_TIMEOUT = 16;
	private static final String[] PODCAST_PROJECTION = { Podcast._ID,
			Podcast.TITLE, Podcast.FEED, Podcast.HTTP_LAST_MODIFIED,
			Podcast.HTTP_EXPIRES, Podcast.HTTP_ETAG };

	public static final String EXTRA_FIRST_SYNC = "first_sync";

	private ThreadPoolExecutor databaseReaderPool;
	private ThreadPoolExecutor feedFetcherPool;
	private ThreadPoolExecutor databaseWriterPool;
	private FeedDownloader feedDownloader;
	private UpdateServiceHelper updateHelper;

	@Override
	public void onCreate() {
		super.onCreate();

		databaseReaderPool = createDatabaseExecutorPool();
		feedFetcherPool = createFeedFetcherPool();
		databaseWriterPool = createDatabaseExecutorPool();
		feedDownloader = new FeedDownloader(this);
		updateHelper = new UpdateServiceHelper(this);

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	// @Override
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

		shutdownPool(databaseReaderPool);
		shutdownPool(feedFetcherPool);
		shutdownPool(databaseWriterPool);
	}

	private ThreadPoolExecutor createDatabaseExecutorPool() {
		return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	private ThreadPoolExecutor createFeedFetcherPool() {
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		return new ThreadPoolExecutor(availableProcessors, availableProcessors,
				0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
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
