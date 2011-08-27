package net.x4a42.volksempfaenger.service;

import net.x4a42.volksempfaenger.PreferenceKeys;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.data.DatabaseHelper;
import net.x4a42.volksempfaenger.net.EnclosureDownloader;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

public class DownloadService extends Service {

	private VolksempfaengerApplication app;
	private DatabaseHelper dbHelper;
	private int phonePlugged;
	private long[] extraId;

	private static final int NETWORK_WIFI = 1;
	private static final int NETWORK_MOBILE = 2;

	private class DownloadTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			Log.d(getClass().getSimpleName(), "doInBackground()");

			SharedPreferences prefs = app.getSharedPreferences();

			int networkAllowd = 0;

			// if automatic downloading is enabled, downloading via WiFi is
			// enabled
			networkAllowd |= NETWORK_WIFI;

			if (!prefs
					.getBoolean(
							PreferenceKeys.DOWNLOAD_WIFI,
							Utils.stringBoolean(getString(R.string.settings_default_download_wifi)))) {
				// downloading is not restricted to WiFi
				networkAllowd |= NETWORK_MOBILE;
			}

			if (extraId == null) {
				// check if automatic downloads are allowed

				if (!prefs
						.getBoolean(
								PreferenceKeys.DOWNLOAD_AUTO,
								Utils.stringBoolean(getString(R.string.settings_default_download_auto)))) {
					// automatic downloading is disabled
					Log.d(getClass().getSimpleName(),
							"automatic downloading is disabled");
					return null;
				}

				if (phonePlugged == 0
						&& prefs.getBoolean(
								PreferenceKeys.DOWNLOAD_CHARGING,
								Utils.stringBoolean(getString(R.string.settings_default_download_charging)))) {
					// downloading is only allowed while charging but phone is
					// not plugged in
					Log.d(getClass().getSimpleName(), "phone is not plugged in");
					return null;
				}

				int networkType = 0;

				// get network state
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

				if (!cm.getBackgroundDataSetting()) {
					// background data is disabled
					Log.d(getClass().getSimpleName(),
							"background data is disabled");
					return null;
				}

				NetworkInfo netInfo = cm.getActiveNetworkInfo();
				if (netInfo != null
						&& netInfo.getState() == NetworkInfo.State.CONNECTED) {
					switch (netInfo.getType()) {
					case ConnectivityManager.TYPE_WIFI:
						networkType = NETWORK_WIFI;
						break;
					case ConnectivityManager.TYPE_MOBILE:
						networkType = NETWORK_MOBILE;
						break;
					}
				}

				if ((networkType & networkAllowd) == 0) {
					// no allowed network connection
					Log.d(getClass().getSimpleName(),
							"network type is not allowed");
					return null;
				}

			}

			// here we can finally start the downloads

			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor cursor;

			if (extraId == null) {
				cursor = db
						.query(DatabaseHelper.Enclosure._TABLE,
								null,
								String.format("%s = ?",
										DatabaseHelper.Enclosure.STATE),
								new String[] { String
										.valueOf(DatabaseHelper.Enclosure.STATE_NEW) },
								null, null, null);
			} else {
				cursor = db.query(DatabaseHelper.Enclosure._TABLE, null, String
						.format("%s = ? AND %s in (%s)",
								DatabaseHelper.Enclosure.STATE,
								DatabaseHelper.Enclosure.ID,
								Utils.joinArray(extraId, ",")),
						new String[] { String
								.valueOf(DatabaseHelper.Enclosure.STATE_NEW) },
						null, null, null);
			}

			EnclosureDownloader ed = new EnclosureDownloader(
					DownloadService.this, (networkAllowd & NETWORK_WIFI) != 0,
					(networkAllowd & NETWORK_MOBILE) != 0);

			int freeSlots = extraId == null ? ed.getFreeDownloadSlots()
					: cursor.getCount();

			Log.d(getClass().getSimpleName(), String.format(
					"starting downloads inQueue:%d freeSlots:%d",
					cursor.getCount(), freeSlots));

			Cursor c;
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.Enclosure.STATE,
					DatabaseHelper.Enclosure.STATE_DOWNLOAD_QUEUED);
			while (cursor.moveToNext() && freeSlots-- > 0) {
				long episodeId = cursor.getLong(cursor
						.getColumnIndex(DatabaseHelper.Enclosure.EPISODE));
				c = db.query(DatabaseHelper.Episode._TABLE, null,
						String.format("%s = ?", DatabaseHelper.Episode.ID),
						new String[] { String.valueOf(episodeId) }, null, null,
						null);
				String title = null;
				if (c.moveToFirst()) {
					title = c.getString(c
							.getColumnIndex(DatabaseHelper.Episode.TITLE));
				}
				c.close();
				long id = cursor.getLong(cursor
						.getColumnIndex(DatabaseHelper.Enclosure.ID));
				String url = cursor.getString(cursor
						.getColumnIndex(DatabaseHelper.Enclosure.URL));
				long downloadId = ed.downloadEnclosure(id, url, title);
				values.put(DatabaseHelper.Enclosure.DOWNLOAD_ID, downloadId);
				db.update(DatabaseHelper.Enclosure._TABLE, values,
						String.format("%s = ?", DatabaseHelper.Enclosure.ID),
						new String[] { String.valueOf(id) });
			}
			cursor.close();

			return null;

		}

		@Override
		protected void onPostExecute(Void result) {

			stopSelf();

		}

	}

	private class BatteryChangedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			unregisterReceiver(this);
			phonePlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

			// the charging state is now known so we can start the DownloadTask
			new DownloadTask().execute();
		}

	}

	@Override
	public void onCreate() {

		super.onCreate();

		app = (VolksempfaengerApplication) getApplication();
		dbHelper = DatabaseHelper.getInstance(this);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.d(getClass().getSimpleName(), "onStartCommand()");

		extraId = intent.getLongArrayExtra("id");
		if (extraId != null && extraId.length == 0) {
			extraId = null;
		}

		// we need to register this broadcast receiver to get the charging state
		registerReceiver(new BatteryChangedReceiver(), new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		return START_STICKY;

	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;

	}

}
