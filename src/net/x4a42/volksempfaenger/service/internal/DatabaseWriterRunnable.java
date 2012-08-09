package net.x4a42.volksempfaenger.service.internal;

public class DatabaseWriterRunnable extends UpdateRunnable {

	public DatabaseWriterRunnable(UpdateState update) {
		super(update);
	}

	@Override
	public void run() {

		// updateService.getUpdateHelper().updatePodcastFromFeed(podcast.id,
		// feed,
		// podcast.firstSync);

		// TODO

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}
		getUpdate().getUpdateService().stopSelf(getUpdate().getStartId());

	}

}
