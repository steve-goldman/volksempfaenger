package net.x4a42.volksempfaenger.service.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.LegacyUpdateServiceHelper;
import net.x4a42.volksempfaenger.feedparser.Feed;

public class DatabaseWriterRunnable extends UpdateRunnable {

	private static final String TAG = "UpdateService";
	private LegacyUpdateServiceHelper databaseHelper;

	private long timeInTransaction = 0;

	public DatabaseWriterRunnable(UpdateState update) {
		super(update);
		databaseHelper = new LegacyUpdateServiceHelper(getUpdate()
				.getUpdateService());
	}

	@Override
	public void run() {

		BlockingQueue<Feed> queue = getUpdate().getDatabaseWriterQueue();
		long startTime = -1;

		try {
			while (true) {
				Feed feed = queue.poll(4, TimeUnit.SECONDS);
				if (feed == null) {
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
					handleFeed(feed);
				}
			}
		} catch (InterruptedException e) {
			Log.i(this, "Exception", e);
		}

		if (startTime != -1) {
			long endTime = System.currentTimeMillis();
			Log.i(TAG, "Finished writing to database (took "
					+ (endTime - startTime) + "ms, " + timeInTransaction
					+ "ms in transaction)");
		}

		getUpdate().stopUpdate();

	}

	private void handleFeed(Feed feed) {
		long startTime = System.currentTimeMillis();
		databaseHelper.updatePodcastFromFeed(feed.localId, feed,
				feed.firstSync);
		long endTime = System.currentTimeMillis();
		timeInTransaction += endTime - startTime;
	}
}
