package net.x4a42.volksempfaenger.service.internal;

import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.service.UpdateService;

public class DatabaseWriter extends UpdateRunnable {

	private Feed feed;

	public DatabaseWriter(UpdateService updateService, PodcastData podcast,
			Feed feed) {
		super(updateService, podcast);
		this.feed = feed;
	}

	@Override
	public void run() {

		updateService.getUpdateHelper().updatePodcastFromFeed(podcast.id, feed,
				podcast.firstSync);

	}

}
