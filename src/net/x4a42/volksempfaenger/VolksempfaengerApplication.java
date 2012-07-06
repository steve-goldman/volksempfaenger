package net.x4a42.volksempfaenger;

import net.x4a42.volksempfaenger.service.CleanCacheService;
import net.x4a42.volksempfaenger.service.UpdateService;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.preference.PreferenceManager;

public class VolksempfaengerApplication extends Application implements
		OnSharedPreferenceChangeListener {
	private SharedPreferences settings;
	private PackageInfo packageinfo;

	@Override
	public void onCreate() {
		super.onCreate();
		packageinfo = getPackageInfo(this);

		settings = PreferenceManager.getDefaultSharedPreferences(this);
		settings.registerOnSharedPreferenceChangeListener(this);

		setUpdateAlarm();
		setCleanCacheAlarm();
	}

	public static PackageInfo getPackageInfo(Context c) {
		try {
			return c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Log.wtf(Log.getTag(VolksempfaengerApplication.class), e);
			return null;
		}
	}

	public static String getDefaultStorageLocation() {
		return Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PODCASTS).getAbsolutePath();
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

		if (interval == 0) {
			Log.d(this, "setUpdateAlarm(): disabled");

			am.cancel(pending);
		} else {
			Log.d(this, "setUpdateAlarm(): " + interval + "ms");

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

	public void setCleanCacheAlarm() {
		Intent intent = new Intent(this, CleanCacheService.class);
		PendingIntent pending = PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC, 0,
				AlarmManager.INTERVAL_HALF_DAY, pending);
	}

	public void onSharedPreferenceChanged(SharedPreferences settings, String key) {
		if (key.equals(PreferenceKeys.DOWNLOAD_INTERVAL)) {
			setUpdateAlarm();
		}
	}
}
