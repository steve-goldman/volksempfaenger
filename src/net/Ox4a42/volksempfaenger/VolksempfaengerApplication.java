package net.Ox4a42.volksempfaenger;

import android.app.Application;
import android.content.SharedPreferences;

public class VolksempfaengerApplication extends Application {
	public SharedPreferences getSharedPreferences() {
		return getSharedPreferences(null, MODE_PRIVATE);
	}
}
