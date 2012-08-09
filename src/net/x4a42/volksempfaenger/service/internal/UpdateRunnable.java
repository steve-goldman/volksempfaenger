package net.x4a42.volksempfaenger.service.internal;

public abstract class UpdateRunnable implements Runnable {

	private UpdateState update;

	public UpdateRunnable(UpdateState update) {
		this.update = update;
	}

	@Override
	public abstract void run();

	public UpdateState getUpdate() {
		return update;
	}

}
