package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ActivityHelper {

	public VolksempfaengerApplication getApp(Activity activity) {
		return (VolksempfaengerApplication) activity.getApplication();
	}

	public static void addGlobalMenu(Activity activity, Menu menu) {
		MenuInflater inflater = activity.getMenuInflater();
		inflater.inflate(R.menu.global, menu);
	}

	public static boolean handleGlobalMenu(Activity activity, MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {

		case R.id.item_settings:
			intent = new Intent(activity, SettingsActivity.class);
			activity.startActivity(intent);
			return true;

		case R.id.item_feedback:
			intent = new Intent(Intent.ACTION_SEND);
			intent.setType("message/rfc822");
			intent.putExtra(Intent.EXTRA_EMAIL,
					net.x4a42.volksempfaenger.Constants.FEEDBACK_TO);
			intent.putExtra(Intent.EXTRA_SUBJECT, "Volksempfänger Feedback");
			activity.startActivity(Intent.createChooser(intent, null));
			return true;

		case android.R.id.home:
			if (activity instanceof OnUpPressedCallback) {
				((OnUpPressedCallback) activity).onUpPressed();
				return true;
			} else {
				return false;
			}

		default:
			return false;

		}
	}

	public static AlertDialog buildErrorDialog(Context context, String title,
			String message) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title).setMessage(message).setCancelable(false)
				.setPositiveButton(R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		final AlertDialog alert = builder.create();
		return alert;
	}

}
