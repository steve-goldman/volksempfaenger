package net.x4a42.volksempfaenger.receiver;

import net.x4a42.volksempfaenger.ui.main.MainActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DownloadNotificationClickedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent newIntent = new Intent(context, MainActivity.class);
		//newIntent.putExtra("tag", MainActivity.TAG_DOWNLOADS);
		newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(newIntent);
	}

}
