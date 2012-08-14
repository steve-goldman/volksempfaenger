package net.x4a42.volksempfaenger.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.net.Uri;

public class UpdateServiceStatus {

	private int globalUpdatesRunning = 0;
	private final List<Uri> singleUpdatesRunning = new ArrayList<Uri>();
	private final Set<UpdateServiceStatusListener> listeners = new HashSet<UpdateServiceStatusListener>();

	protected UpdateServiceStatus() {
	}

	public synchronized void registerUpdateServiceStatusListener(
			UpdateServiceStatusListener listener) {
		if (globalUpdatesRunning > 0) {
			listener.onGlobalUpdateStarted();
		}
		for (Uri podcast : singleUpdatesRunning) {
			listener.onSingleUpdateStarted(podcast);
		}
		listeners.add(listener);
	}

	public synchronized void unregisterUpdateServiceStatusListener(
			UpdateServiceStatusListener listener) {
		listeners.remove(listener);
	}

	public synchronized void startGlobalUpdate() {

		if (globalUpdatesRunning++ == 0) {
			for (UpdateServiceStatusListener listener : listeners) {
				listener.onGlobalUpdateStarted();
			}
		}

	}

	public synchronized void stopGlobalUpdate() {

		if (--globalUpdatesRunning == 0) {
			for (UpdateServiceStatusListener listener : listeners) {
				listener.onGlobalUpdateStopped();
			}
		}

	}

	public synchronized void startSingleUpdate(Uri podcast) {

		if (!singleUpdatesRunning.contains(podcast)) {
			for (UpdateServiceStatusListener listener : listeners) {
				listener.onSingleUpdateStarted(podcast);
			}
		}
		singleUpdatesRunning.add(podcast);

	}

	public synchronized void stopSingleUpdate(Uri podcast) {

		singleUpdatesRunning.remove(podcast);
		if (!singleUpdatesRunning.contains(podcast)) {
			for (UpdateServiceStatusListener listener : listeners) {
				listener.onSingleUpdateStopped(podcast);
			}
		}

	}

	public static interface UpdateServiceStatusListener {

		public void onGlobalUpdateStarted();

		public void onGlobalUpdateStopped();

		public void onSingleUpdateStarted(Uri podcast);

		public void onSingleUpdateStopped(Uri podcast);

	}

	public static abstract class GlobalUpdateListener implements
			UpdateServiceStatusListener {

		@Override
		public void onSingleUpdateStarted(Uri podcast) {
		}

		@Override
		public void onSingleUpdateStopped(Uri podcast) {
		}

	}

	public static abstract class SingleUpdateListener implements
			UpdateServiceStatusListener {

		@Override
		public void onGlobalUpdateStarted() {
		}

		@Override
		public void onGlobalUpdateStopped() {
		}

	}

	public static abstract class SimpleUpdateListener implements
			UpdateServiceStatusListener {

		@Override
		public void onGlobalUpdateStarted() {
			onUpdateStarted(null);
		}

		@Override
		public void onGlobalUpdateStopped() {
			onUpdateStopped(null);
		}

		@Override
		public void onSingleUpdateStarted(Uri podcast) {
			onUpdateStarted(podcast);
		}

		@Override
		public void onSingleUpdateStopped(Uri podcast) {
			onUpdateStopped(podcast);
		}

		public abstract void onUpdateStarted(Uri podcast);

		public abstract void onUpdateStopped(Uri podcast);

	}

	public static class UiThreadUpdateServiceStatusListenerWrapper implements
			UpdateServiceStatusListener {

		private Activity activity;
		private UpdateServiceStatusListener listener;

		public UiThreadUpdateServiceStatusListenerWrapper(Activity activity,
				UpdateServiceStatusListener listener) {
			if (activity == null) {
				throw new NullPointerException("activity must not be null");
			}
			if (listener == null) {
				throw new NullPointerException("listener must not be null");
			}
			this.activity = activity;
			this.listener = listener;
		}

		@Override
		public void onGlobalUpdateStarted() {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					listener.onGlobalUpdateStarted();
				}

			});
		}

		@Override
		public void onGlobalUpdateStopped() {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					listener.onGlobalUpdateStopped();
				}

			});
		}

		@Override
		public void onSingleUpdateStarted(final Uri podcast) {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					listener.onSingleUpdateStarted(podcast);
				}

			});
		}

		@Override
		public void onSingleUpdateStopped(final Uri podcast) {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					listener.onSingleUpdateStopped(podcast);
				}

			});
		}

	}

}
