package net.x4a42.volksempfaenger.service.internal;

import net.x4a42.volksempfaenger.service.UpdateService;

public abstract class UpdateRunnable implements Runnable {

	protected UpdateService updateService;
	protected PodcastData podcast;

	public UpdateRunnable(UpdateService updateService, PodcastData podcast) {
		this.updateService = updateService;
		this.podcast = podcast;
	}

	@Override
	public abstract void run();

}
