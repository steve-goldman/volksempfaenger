package net.x4a42.volksempfaenger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import net.x4a42.volksempfaenger.service.CleanCacheService;
import net.x4a42.volksempfaenger.service.UpdateService;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FlushedInputStream;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

public class VolksempfaengerApplication extends Application implements
		OnSharedPreferenceChangeListener, ComponentCallbacks2 {
	private SharedPreferences settings;
	private PackageInfo packageinfo;

	public ImageLoader imageLoader;

	@Override
	public void onCreate() {
		super.onCreate();
		packageinfo = getPackageInfo(this);

		settings = PreferenceManager.getDefaultSharedPreferences(this);
		settings.registerOnSharedPreferenceChangeListener(this);

		setUpdateAlarm();
		setCleanCacheAlarm();

		initImageLoader();
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
		return String.format(Locale.getDefault(), "%s (%d)", getVersionName(),
				getVersion());
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
			Log.v(this, "setUpdateAlarm(): disabled");

			am.cancel(pending);
		} else {
			Log.v(this, "setUpdateAlarm(): " + interval + "ms");

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

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		if (level >= TRIM_MEMORY_MODERATE) {
			imageLoader.clearMemoryCache();
		} else if (level >= TRIM_MEMORY_BACKGROUND) {
		}
	}

	private void initImageLoader() {
		imageLoader = ImageLoader.getInstance();
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		int memoryClassBytes = am.getMemoryClass() * 1024 * 1024;
		int maxSize = 2 * getResources().getDimensionPixelSize(
				R.dimen.grid_column_width);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).memoryCacheSize(memoryClassBytes / 4)
				.discCacheSize(Constants.LOGO_DISC_CACHE_SIZE)
				.memoryCacheExtraOptions(maxSize, maxSize)
				.threadPoolSize(Runtime.getRuntime().availableProcessors())
				.threadPriority(Thread.MAX_PRIORITY)
				.imageDownloader(new HttpURLConnctionImageDownloader()).build();
		imageLoader.init(config);
	}

	private class HttpURLConnctionImageDownloader implements ImageDownloader {

		@Override
		public InputStream getStream(String imageUri, Object extra)
				throws IOException {
			URLConnection connection = new URL(imageUri).openConnection();
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection conn = (HttpURLConnection) connection;
				if (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
						|| conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {

					conn = (HttpURLConnection) new URL(
							conn.getHeaderField("Location")).openConnection();
				}
				return new FlushedInputStream(new BufferedInputStream(
						connection.getInputStream()));
			} else {
				return new BufferedInputStream(connection.getInputStream());
			}
		}
	}
}
