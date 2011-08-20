package net.Ox4a42.volksempfaenger;

import net.Ox4a42.volksempfaenger.data.DbHelper;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class VolksempfaengerApplication extends Application {
	private SharedPreferences settings;
	private DbHelper dbHelper;
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
		dbHelper = new DbHelper(this);
	}

	public SharedPreferences getSharedPreferences() {
		return settings;
	}
	
	public SQLiteDatabase getReadableDatabase() {
		return dbHelper.getReadableDatabase();
	}
	
	public SQLiteDatabase getWritableDatabase() {
		return dbHelper.getWritableDatabase();
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
