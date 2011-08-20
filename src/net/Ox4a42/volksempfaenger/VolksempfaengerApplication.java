package net.Ox4a42.volksempfaenger;

import net.Ox4a42.volksempfaenger.data.DbHelper;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class VolksempfaengerApplication extends Application {
	private SharedPreferences settings;
	private DbHelper dbhelper;
	private PackageInfo packageinfo;

	@Override
	public void onCreate() {
		super.onCreate();
		// get package info
		try {
			packageinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Log.wtf(getClass().getName(), e);
		}
		// get shared preferences
		settings = getSharedPreferences(null, MODE_PRIVATE);
		// open database
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
	
	public int getVersion() {
		return packageinfo.versionCode;
	}
	
	public String getVersionName() {
		return packageinfo.versionName;
	}
	
	public String getVersionString() {
		return String.format("%s (%d)", getVersionName(), getVersion());
	}
}
