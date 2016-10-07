package net.x4a42.volksempfaenger.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.x4a42.volksempfaenger.R;

public class ActivityHelper {

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
