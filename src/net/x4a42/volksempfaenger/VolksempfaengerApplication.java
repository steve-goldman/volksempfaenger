package net.x4a42.volksempfaenger;

import android.app.Application;
import android.content.Context;
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
		packageinfo = getPackageInfo(this);
		// get shared preferences
		settings = getSharedPreferences(null, MODE_PRIVATE);
	}

	public static PackageInfo getPackageInfo(Context c) {
		try {
			return c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Log.wtf(VolksempfaengerApplication.class.getName(), e);
			return null;
		}
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
