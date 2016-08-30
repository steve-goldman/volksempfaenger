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

	/**
	 * Register a listener. Some callbacks will already be called from within
	 * this method as documented in UpdateServiceStatusListener.
	 * 
	 * @param listener
	 *            UpdateServiceStatusListener to register, must not be null.
	 */
	public synchronized void registerUpdateServiceStatusListener(
			UpdateServiceStatusListener listener) {

		if (globalUpdatesRunning > 0) {
			listener.onGlobalUpdateStarted();
		} else {
			listener.onGlobalUpdateStopped();
		}

		if (!singleUpdatesRunning.isEmpty()) {
			for (Uri podcast : singleUpdatesRunning) {
				listener.onSingleUpdateStarted(podcast);
			}
		} else {
			listener.onSingleUpdateStopped(null);
		}

		listeners.add(listener);

	}

	/**
	 * Unregister a listener.
	 * 
	 * @param listener
	 *            UpdateServiceStatusListener to unregister.
	 */
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

		/**
		 * This method will be called when a global update gets started or if a
		 * global update is already running at the time of registering this
		 * listener. In this case this method will be called from within the
		 * register method.
		 */
		public void onGlobalUpdateStarted();

		/**
		 * This method will be called when a global update finishes or if there
		 * is no global update running at the time of registering this listener.
		 * In this case this method will be called from within the register
		 * method.
		 */
		public void onGlobalUpdateStopped();

		/**
		 * This method will be called for every single update that gets started.
		 * If there are any single updates running at the time this listener
		 * gets registered, this method will be called from within the register
		 * method for every one with the Uri of the podcast being updated as an
		 * parameter.
		 * 
		 * @param podcast
		 *            The Uri of the podcast being updated.
		 */
		public void onSingleUpdateStarted(Uri podcast);

		/**
		 * This method will be called when a single update stops. If there are
		 * no single updates running at the time of registering this receiver,
		 * this method will be called with a null parameter from within the
		 * register method.
		 * 
		 * @param podcast
		 *            The Uri of the podcast that finished updating or null if
		 *            there are no single updates running at the time of
		 *            registering this receiver.
		 */
		public void onSingleUpdateStopped(Uri podcast);

	}

	/**
	 * Abstract implementation of UpdateServiceStatusListener that just drops
	 * all callbacks received for single updates.
	 */
	public static abstract class GlobalUpdateListener implements
			UpdateServiceStatusListener {

		@Override
		public void onSingleUpdateStarted(Uri podcast) {
		}

		@Override
		public void onSingleUpdateStopped(Uri podcast) {
		}

	}

	/**
	 * Abstract implementation of UpdateServiceStatusListener that just drops
	 * all callbacks received for global updates.
	 */
	public static abstract class SingleUpdateListener implements
			UpdateServiceStatusListener {

		@Override
		public void onGlobalUpdateStarted() {
		}

		@Override
		public void onGlobalUpdateStopped() {
		}

	}

	/**
	 * Wrapper for UpdateServiceStatusListener that runs all callbacks on the UI
	 * thread of an Activity.
	 */
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
