package net.x4a42.volksempfaenger.service;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.LegacyUpdateServiceHelper;
import net.x4a42.volksempfaenger.misc.SimpleThreadFactory;
import net.x4a42.volksempfaenger.net.FeedDownloader;
import net.x4a42.volksempfaenger.service.internal.DatabaseReaderRunnable;
import net.x4a42.volksempfaenger.service.internal.DatabaseWriterRunnable;
import net.x4a42.volksempfaenger.service.internal.FeedDownloaderRunnable;
import net.x4a42.volksempfaenger.service.internal.FeedParserRunnable;
import net.x4a42.volksempfaenger.service.internal.PodcastData;
import net.x4a42.volksempfaenger.service.internal.UpdateState;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

// TODO
public class UpdateService extends Service {

	private static final String TAG = "UpdateService";
	private static final long AWAIT_TERMINATION_TIMEOUT = 16000;
	private static final long THREAD_KEEP_ALIVE_TIME = 8000;

	public static final String EXTRA_FIRST_SYNC = "first_sync";

	private ThreadPoolExecutor databaseReaderPool;
	private ThreadPoolExecutor feedDownloaderPool;
	private ThreadPoolExecutor feedParserPool;
	private ThreadPoolExecutor databaseWriterPool;
	private FeedDownloader feedDownloader;
	private LegacyUpdateServiceHelper updateHelper;

	@Override
	public void onCreate() {
		super.onCreate();

		Log.d(TAG, "Creating UpdateService");

		databaseReaderPool = createThreadPool("UpdateService.DatabaseReader", 1);
		feedDownloaderPool = createThreadPool("UpdateService.FeedDownloader", 4);
		feedParserPool = createThreadPool("UpdateService.FeedParser", Runtime
				.getRuntime().availableProcessors());
		databaseWriterPool = createThreadPool("UpdateService.DatabaseWriter", 1);
		feedDownloader = new FeedDownloader(this);
		updateHelper = new LegacyUpdateServiceHelper(this);

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Starting update for " + intent.toString());
		UpdateState update = new UpdateState(this, intent, startId);
		enqueueUpdate(update);
		return START_REDELIVER_INTENT;
	}

	public void enqueueUpdate(UpdateState update) {
		enqueueDatabaseReader(update);
		enqueueDatabaseWriter(update);
	}

	public void enqueueDatabaseReader(UpdateState update) {
		databaseReaderPool.execute(new DatabaseReaderRunnable(update));
	}

	public void enqueueFeedDownloader(UpdateState update, PodcastData podcast) {
		feedDownloaderPool.execute(new FeedDownloaderRunnable(update, podcast));
	}

	public void enqueueFeedParser(UpdateState update, PodcastData podcast,
			File feed) {
		feedParserPool.execute(new FeedParserRunnable(update, podcast, feed));
	}

	public void enqueueDatabaseWriter(UpdateState update) {
		databaseWriterPool.execute(new DatabaseWriterRunnable(update));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(TAG, "Destroying UpdateService");

		shutdownPool(databaseReaderPool);
		shutdownPool(feedDownloaderPool);
		shutdownPool(feedParserPool);
		shutdownPool(databaseWriterPool);
	}

	private ThreadPoolExecutor createThreadPool(String name, int threads) {
		return new ThreadPoolExecutor(threads, threads, THREAD_KEEP_ALIVE_TIME,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
				new SimpleThreadFactory(name));
	}

	private void shutdownPool(ExecutorService pool) {
		try {
			pool.shutdown();
			pool.awaitTermination(AWAIT_TERMINATION_TIMEOUT,
					TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Log.w(TAG, "Catched InterruptedException:", e);
		}
	}

	public FeedDownloader getFeedDownloader() {
		return feedDownloader;
	}

	public LegacyUpdateServiceHelper getUpdateHelper() {
		return updateHelper;
	}

}
