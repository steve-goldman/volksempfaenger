package net.x4a42.volksempfaenger.service.internal;

import net.x4a42.volksempfaenger.service.UpdateService;
import android.content.Intent;

public class DatabaseReader extends UpdateRunnable {

	private Intent intent;

	public DatabaseReader(UpdateService updateService, PodcastData podcast,
			Intent intent) {
		super(updateService, podcast);
		this.intent = intent;
	}

	@Override
	public void run() {


	}

}
