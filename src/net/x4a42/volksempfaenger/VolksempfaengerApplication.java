package net.x4a42.volksempfaenger;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class VolksempfaengerApplication extends Application {
	private SharedPreferences settings;
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
	}

	public SharedPreferences getSharedPreferences() {
		return settings;
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
