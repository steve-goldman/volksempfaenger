package net.x4a42.volksempfaenger.service.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.LegacyUpdateServiceHelper;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.service.UpdateService;

public class DatabaseWriterRunnable extends UpdateRunnable {

	private static final String TAG = "UpdateService";
	private LegacyUpdateServiceHelper databaseHelper;

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
					// ensureTransaction();
					handleFeed(feed);
				}
			}
		} catch (InterruptedException e) {
			Log.i(this, "Exception", e);
		}

		// finishTransaction();

		if (startTime != -1) {
			long endTime = System.currentTimeMillis();
			Log.i(TAG, "Finished writing to database (took "
					+ (endTime - startTime) + "ms)");
		}

		getUpdate().stopUpdate();

	}

	// private void ensureTransaction() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// private void finishTransaction() {
	// // TODO Auto-generated method stub
	//
	// }

	private void handleFeed(Feed feed) {
		databaseHelper.updatePodcastFromFeed(
				feed.local_id,
				feed,
				getUpdate().getIntent().getBooleanExtra(
						UpdateService.EXTRA_FIRST_SYNC, false));
	}
}
