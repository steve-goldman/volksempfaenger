package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import android.app.Activity;
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
		switch (item.getItemId()) {

		case R.id.item_settings:
			activity.startActivity(new Intent(activity, SettingsActivity.class));
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

}
