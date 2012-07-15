package net.x4a42.volksempfaenger.receiver;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.ui.MainActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BackgroundErrorReceiver extends BroadcastReceiver {
	private final static int NOTIFICATION_ID = 0x5dc2060d;
	private static final String MAIN_PACKAGE = "net.x4a42.volksempfaenger";

	public static final String ACTION_BACKGROUND_ERROR = MAIN_PACKAGE
			+ ".BACKGROUND_ERROR";
	public static final String EXTRA_ERROR_TITLE = MAIN_PACKAGE
			+ ".ERROR_TITLE";
	public static final String EXTRA_ERROR_TEXT = MAIN_PACKAGE + ".ERROR_TEXT";
	public static final String EXTRA_ERROR_ID = MAIN_PACKAGE + ".ERROR_ID";

	public static final int ERROR_IMPORT = 1;

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

}
