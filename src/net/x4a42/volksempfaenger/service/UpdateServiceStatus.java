package net.x4a42.volksempfaenger.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

public class UpdateServiceStatus {

	public static final String TAG = "UpdateSeriviceStatus";
	private static boolean isLocked = false;
	private static boolean isUpdating = false;
	private static Uri uri = null;
	private static final Set<Receiver> receivers = Collections
			.synchronizedSet(new HashSet<Receiver>());

	private UpdateServiceStatus() {
		throw new UnsupportedOperationException();
	}

	public static synchronized void lock() {
		while (isLocked) {
			try {
				UpdateServiceStatus.class.wait();
			} catch (InterruptedException e) {
				Log.w(TAG, e);
			}
		}
		isLocked = true;
	}

	public static synchronized void unlock() {
		isLocked = false;
		UpdateServiceStatus.class.notify();
	}

	public static void startUpdate() {
		isUpdating = true;
		sendStatus(new Status(true, null));
	}

	public static void startUpdate(Uri uri) {
		UpdateServiceStatus.uri = uri;
		sendStatus(new Status(true, uri));
	}

	public static void stopUpdate(Uri uri) {
		if (UpdateServiceStatus.uri.equals(uri)) {
			UpdateServiceStatus.uri = null;
		}
		sendStatus(new Status(false, uri));
	}

	public static void stopUpdate() {
		isUpdating = false;
		uri = null;
		sendStatus(new Status(false, null));
	}

	public static void registerReceiver(Receiver receiver) {
		if (isUpdating) {
			receiver.receive(new Status(true, uri));
		} else {
			receiver.receive(new Status(false, null));
		}
		receivers.add(receiver);
	}

	public static void unregisterReceiver(Receiver receiver) {
		receivers.remove(receiver);
	}

	private static void sendStatus(Status status) {
		for (Receiver receiver : receivers) {
			receiver.receive(status);
		}
	}

	public static class Status {
		private boolean isUpdating;
		private Uri uri;

		public Status(boolean isUpdating, Uri uri) {
			this.isUpdating = isUpdating;
			this.uri = uri;
		}

		@Override
		public String toString() {
			return getClass().getName() + "[" + "isUpdating=" + isUpdating()
					+ ", " + "uri=" + (uri == null ? uri : uri.toString())
					+ "]";
		}

		public boolean isUpdating() {
			return isUpdating;
		}

		public Uri getUri() {
			return uri;
		}
	}

	public static abstract class Receiver {
		public abstract void receive(Status status);
	}

	public static abstract class UiReceiver extends Receiver {

		private Activity activity;

		public final void setActivity(Activity activity) {
			this.activity = activity;
		}

		@Override
		public final void receive(final Status status) {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					receiveUi(status);
				}
			});
		}

		public abstract void receiveUi(Status status);
	}

}
