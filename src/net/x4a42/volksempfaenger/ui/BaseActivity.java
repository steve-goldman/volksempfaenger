package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends Activity {
	protected final String TAG = getClass().getSimpleName();

	public VolksempfaengerApplication getApp() {
		return (VolksempfaengerApplication) super.getApplication();
	}

	public static void addGlobalMenu(Activity activity, Menu menu) {
		MenuInflater inflater = activity.getMenuInflater();
		inflater.inflate(R.menu.global, menu);
	}

	public void addGlobalMenu(Menu menu) {
		addGlobalMenu(this, menu);
	}

	public static boolean handleGlobalMenu(Activity activity, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_settings:
			activity.startActivity(new Intent(activity, SettingsActivity.class));
			return true;
		default:
			return false;
		}
	}

	public boolean handleGlobalMenu(MenuItem item) {
		return handleGlobalMenu(this, item);
	}
}
