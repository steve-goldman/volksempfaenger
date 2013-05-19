package net.x4a42.volksempfaenger.service.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.service.DownloadService;
import net.x4a42.volksempfaenger.service.UpdateService;
import android.content.Intent;
import android.util.Log;

public class UpdateState {

	private static final String TAG = "UpdateService";

	private UpdateService updateService;
	private Intent intent;
	private int startId;
	private BlockingQueue<Feed> databaseWriterQueue = new LinkedBlockingQueue<Feed>();
	private AtomicInteger remainingFeedCounter = new AtomicInteger();
	private AtomicBoolean remainingFeedCounterSet = new AtomicBoolean();
	private long startTime;
	private boolean globalUpdate;

	public UpdateState(UpdateService updateService, Intent intent, int startId) {
		this.updateService = updateService;
		this.intent = intent;
		this.startId = startId;
		globalUpdate = intent.getData() == null;
		Log.i(TAG, "Starting update");
	}

	public void startUpdate() {
		if (globalUpdate) {
			UpdateService.Status.startGlobalUpdate();
		} else {
			UpdateService.Status.startSingleUpdate(intent.getData());
		}
		startTime = System.currentTimeMillis();
		updateService.enqueueUpdate(this);
	}

	public void stopUpdate() {
		long endTime = System.currentTimeMillis();
		Log.i(TAG, "Finished update (took " + (endTime - startTime) + "ms)");

		if (globalUpdate) {
			UpdateService.Status.stopGlobalUpdate();
		} else {
			UpdateService.Status.stopSingleUpdate(intent.getData());
		}

		// start DownloadService for automatic downloads
		updateService.startService(new Intent(updateService,
				DownloadService.class));

		// stop UpdateService
		updateService.stopSelf(startId);
	}

	public UpdateService getUpdateService() {
		return updateService;
	}

	public Intent getIntent() {
		return intent;
	}

	public int getStartId() {
		return startId;
	}

	public BlockingQueue<Feed> getDatabaseWriterQueue() {
		return databaseWriterQueue;
	}

	public void setRemainingFeedCounter(int value) {
		if (remainingFeedCounterSet.compareAndSet(false, true)) {
			remainingFeedCounter.addAndGet(value);
		}
	}

	public void decrementRemainingFeedCounter() {
		remainingFeedCounter.decrementAndGet();
	}

	public int getRemainingFeedCounter() {
		return remainingFeedCounter.get();
	}

}
