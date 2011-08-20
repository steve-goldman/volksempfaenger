package net.Ox4a42.volksempfaenger.ui;

import net.Ox4a42.volksempfaenger.R;
import net.Ox4a42.volksempfaenger.VolksempfaengerApplication;
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

	public void handleGlobalMenu(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			break;
		}
	}
}
