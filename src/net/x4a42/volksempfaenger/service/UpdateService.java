package net.x4a42.volksempfaenger.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.UpdateServiceHelper;
import net.x4a42.volksempfaenger.net.FeedDownloader;
import net.x4a42.volksempfaenger.service.internal.DatabaseReaderRunnable;
import net.x4a42.volksempfaenger.service.internal.DatabaseWriterRunnable;
import net.x4a42.volksempfaenger.service.internal.FeedDownloaderRunnable;
import net.x4a42.volksempfaenger.service.internal.PodcastData;
import net.x4a42.volksempfaenger.service.internal.UpdateState;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

// TODO
public class UpdateService extends Service {

	private static final long AWAIT_TERMINATION_TIMEOUT = 16000;
	private static final long THREAD_KEEP_ALIVE_TIME = 8000;

	public static final String EXTRA_FIRST_SYNC = "first_sync";

	private ThreadPoolExecutor databaseReaderPool;
	private ThreadPoolExecutor feedDownloaderPool;
	private ThreadPoolExecutor feedParserPool;
	private ThreadPoolExecutor databaseWriterPool;
	private FeedDownloader feedDownloader;
	private UpdateServiceHelper updateHelper;

	@Override
	public void onCreate() {
		super.onCreate();

		databaseReaderPool = createThreadPool(1);
		feedDownloaderPool = createThreadPool(4);
		feedParserPool = createThreadPool(Runtime.getRuntime()
				.availableProcessors());
		databaseWriterPool = createThreadPool(1);
		feedDownloader = new FeedDownloader(this);
		updateHelper = new UpdateServiceHelper(this);

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
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
			Object feed) {

	}

	public void enqueueDatabaseWriter(UpdateState update) {
		databaseWriterPool.execute(new DatabaseWriterRunnable(update));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		shutdownPool(databaseReaderPool);
		shutdownPool(feedDownloaderPool);
		shutdownPool(feedParserPool);
		shutdownPool(databaseWriterPool);
	}

	private ThreadPoolExecutor createThreadPool(int threads) {
		return new ThreadPoolExecutor(threads, threads, THREAD_KEEP_ALIVE_TIME,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	private void shutdownPool(ExecutorService pool) {
		try {
			pool.shutdown();
			pool.awaitTermination(AWAIT_TERMINATION_TIMEOUT,
					TimeUnit.MILLISECONDS);
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
