package net.x4a42.volksempfaenger.service.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import net.x4a42.volksempfaenger.Log;

public class DatabaseWriterRunnable extends UpdateRunnable {

	private static final String TAG = "UpdateService";

	public DatabaseWriterRunnable(UpdateState update) {
		super(update);
	}

	@Override
	public void run() {

		BlockingQueue<Object> queue = getUpdate().getDatabaseWriterQueue();
		long startTime = -1;

		try {
			while (true) {
				Object o = queue.poll(4, TimeUnit.SECONDS);
				if (o == null) {
					if (getUpdate().getRemainingFeedCounter() == 0) {
						break;
					} else {
						continue;
					}
				} else {
					if (startTime == -1) {
						startTime = System.currentTimeMillis();
						Log.i(TAG, "Started writing to database");
					}
					ensureTransaction();
					handleObject(o);
				}
			}
		} catch (InterruptedException e) {
			Log.i(this, "Exception", e);
		}

		finishTransaction();

		if (startTime != -1) {
			long endTime = System.currentTimeMillis();
			Log.i(TAG, "Finished writing to database (took "
					+ (endTime - startTime) + "ms)");
		}

		getUpdate().stopUpdate();

	}

	private void ensureTransaction() {
		// TODO Auto-generated method stub

	}

	private void finishTransaction() {
		// TODO Auto-generated method stub

	}

	private void handleObject(Object o) {
		// TODO Auto-generated method stub
		// Log.d(this, o.toString());
		// updateService.getUpdateHelper().updatePodcastFromFeed(podcast.id,
		// feed,
		// podcast.firstSync);
	}
}
