package net.Ox4a42.volksempfaenger;

import net.Ox4a42.volksempfaenger.data.DbHelper;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

public class VolksempfaengerApplication extends Application {
	private SharedPreferences settings;
	private DbHelper dbhelper;
	
	@Override
	public void onCreate() {
		super.onCreate();
		settings = getSharedPreferences(null, MODE_PRIVATE);
		dbhelper = new DbHelper(this);
		// Just to ensure that the database gets created.
		// TODO: Remove this later
		dbhelper.getReadableDatabase();
	}

	public SharedPreferences getSharedPreferences() {
		return settings;
	}
	
	public DbHelper getDbHelper() {
		return dbhelper;
	}
}
