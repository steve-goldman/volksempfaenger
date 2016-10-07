package net.x4a42.volksempfaenger.receiver;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.ui.main.MainActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BackgroundErrorReceiver extends BroadcastReceiver {
	private final static int NOTIFICATION_ID = 0x5dc2060d;

	public static final String ACTION_BACKGROUND_ERROR = "net.x4a42.volksempfaenger.BACKGROUND_ERROR";
	public static final String EXTRA_ERROR_TITLE = "ERROR_TITLE";
	public static final String EXTRA_ERROR_TEXT = "ERROR_TEXT";
	public static final String EXTRA_ERROR_ID = "ERROR_ID";

	public static final int ERROR_IMPORT = 1;
	public static final int ERROR_ADD = 2;

	@Override
	public void onReceive(Context context, Intent intent) {
		String title = intent.getStringExtra(EXTRA_ERROR_TITLE);
		if (title == null) {
			return;
		}
		String text = intent.getStringExtra(EXTRA_ERROR_TEXT);
		intent.setClass(context, MainActivity.class);
		Notification.Builder nb = new Notification.Builder(context)
				.setContentTitle(title)
				.setSmallIcon(R.drawable.notification)
				.setAutoCancel(true)
				.setContentIntent(
						PendingIntent.getActivity(context, 0, intent, 0));
		if (text != null) {
			nb.setContentText(text);
		}
		Notification notification = Utils.notificationFromBuilder(nb);
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(NOTIFICATION_ID, notification);
	}

	public static Intent getBackgroundErrorIntent(String title, String message,
			int id) {
		Intent intent = new Intent(ACTION_BACKGROUND_ERROR);
		intent.putExtra(EXTRA_ERROR_TITLE, title);
		if (message != null) {
			intent.putExtra(EXTRA_ERROR_TEXT, message);
		}
		if (id != 0) {
			intent.putExtra(EXTRA_ERROR_ID, ERROR_IMPORT);
		}
		return intent;
	}

}
