package net.x4a42.volksempfaenger.service.internal;

import net.x4a42.volksempfaenger.service.UpdateService;
import android.content.Intent;

public class UpdateState {

	private UpdateService updateService;
	private Intent intent;
	private int startId;

	public UpdateState(UpdateService updateService, Intent intent, int startId) {
		this.updateService = updateService;
		this.intent = intent;
		this.startId = startId;
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

}
