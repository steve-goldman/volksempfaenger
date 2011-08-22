package net.x4a42.volksempfaenger;

import net.x4a42.volksempfaenger.data.UpdateService;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;

public class VolksempfaengerApplication extends Application implements
		OnSharedPreferenceChangeListener {
	private SharedPreferences settings;
	private PackageInfo packageinfo;

	@Override
	public void onCreate() {
		super.onCreate();
		// get package info
		packageinfo = getPackageInfo(this);
		// get shared preferences
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		settings.registerOnSharedPreferenceChangeListener(this);
		// set update alarm
		setUpdateAlarm();
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

	public void setUpdateAlarm() {
		long interval;
		long intervalDefault;
		String intervalString;

		intervalDefault = Long
				.parseLong(getString(R.string.settings_default_download_interval));
		intervalString = settings.getString(PreferenceKeys.DOWNLOAD_INTERVAL,
				null);
		try {
			interval = Long.parseLong(intervalString);
		} catch (NumberFormatException e) {
			interval = intervalDefault;
		}

		Intent intent = new Intent(this, UpdateService.class);
		PendingIntent pending = PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

		Log.d(getClass().getSimpleName(), "setUpdateAlarm("+interval+")");
		
		if (interval == 0) {
			am.cancel(pending);
		} else {
			long next;
			long last = UpdateService.getLastRun();

			if (last == 0) {
				next = System.currentTimeMillis() + (interval / 2);
			} else {
				next = last + interval;
			}

			am.setInexactRepeating(AlarmManager.RTC, next, interval, pending);
		}
	}

	public void onSharedPreferenceChanged(SharedPreferences settings, String key) {
		Log.d(getClass().getSimpleName(), "onSharedPreferenceChanged(" + key
				+ ")");

		if (key.equals(PreferenceKeys.DOWNLOAD_INTERVAL)) {
			setUpdateAlarm();
		}
	}
}
