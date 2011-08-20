package net.Ox4a42.volksempfaenger.ui;

import net.Ox4a42.volksempfaenger.VolksempfaengerApplication;
import android.app.Activity;

public class BaseActivity extends Activity {
	public VolksempfaengerApplication getApp() {
		return (VolksempfaengerApplication) super.getApplication();
	}
}
