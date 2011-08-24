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

	public void addGlobalMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.global, menu);
	}

	public boolean handleGlobalMenu(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return false;
		}
	}
}
