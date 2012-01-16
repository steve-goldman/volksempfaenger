package net.x4a42.volksempfaenger.service;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import net.x4a42.volksempfaenger.PreferenceKeys;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.data.DatabaseHelper;
import net.x4a42.volksempfaenger.net.EnclosureDownloader;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
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
import android.widget.Toast;

public class DownloadService extends Service {

	private VolksempfaengerApplication app;
	private DatabaseHelper dbHelper;
	private int phonePlugged;

	private static final int NETWORK_WIFI = 1;
	private static final int NETWORK_MOBILE = 2;

	private class DownloadTask extends AsyncTask<Long, Integer, Void> {

		@Override
		protected Void doInBackground(Long... params) {

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

			if (params == null) {
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

			String selection = DatabaseHelper.ExtendedEpisode.ENCLOSURE_URL
					+ " IS NOT NULL";
			String orderBy = null;

			if (params == null) {
				selection += " AND "
						+ DatabaseHelper.ExtendedEpisode.EPISODE_STATE + " = "
						+ DatabaseHelper.Episode.STATE_NEW;

				orderBy = String.format("%s DESC",
						DatabaseHelper.ExtendedEpisode.EPISODE_DATE);
			} else {
				selection += " AND "
						+ DatabaseHelper.ExtendedEpisode.ID + " IN ("
						+ Utils.joinArray(params, ",") + ")";
			}

			Cursor cursor = db.query(DatabaseHelper.ExtendedEpisode._TABLE,
					null, selection, null, null, null, orderBy);

			EnclosureDownloader ed = new EnclosureDownloader(
					DownloadService.this, (networkAllowd & NETWORK_WIFI) != 0,
					(networkAllowd & NETWORK_MOBILE) != 0);
			DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

			int freeSlots = params == null ? ed.getFreeDownloadSlots() : cursor
					.getCount();

			Log.d(getClass().getSimpleName(), String.format(
					"starting downloads inQueue:%d freeSlots:%d",
					cursor.getCount(), freeSlots));

			ContentValues values = new ContentValues();
			while (cursor.moveToNext() && freeSlots-- > 0) {
				long downloadId = cursor
						.getLong(cursor
								.getColumnIndex(DatabaseHelper.ExtendedEpisode.DOWNLOAD_ID));
				Query query = new Query();
				query.setFilterById(downloadId);
				Cursor dmCursor = dm.query(query);
				if (dmCursor.moveToFirst()) {
					// The Download of this episode was already started
					int status = dmCursor.getInt(dmCursor
							.getColumnIndex(DownloadManager.COLUMN_STATUS));
					switch (status) {
					case DownloadManager.STATUS_SUCCESSFUL:
						try {
							URI localUri = new URI(
									dmCursor.getString(dmCursor
											.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
							if (new File(localUri).isFile()) {
								// the file was successfully downloaded and does
								// still exist
								if (params != null) {
									publishProgress(R.string.message_download_episode_already_downloaded);
								}
								freeSlots++;
								continue;
							} else {
								// the file was deleted, we'll restart the
								// download
								break;
							}
						} catch (URISyntaxException e) {
							// this should never ever happen but just in case
							// we'll handle it like a failed download (next
							// case)
						}
					case DownloadManager.STATUS_FAILED:
						// remove the download so that we can start a new one
						dm.remove(downloadId);
						break;

					case DownloadManager.STATUS_PENDING:
					case DownloadManager.STATUS_RUNNING:
					case DownloadManager.STATUS_PAUSED:
						// the download is already running
						if (params != null) {
							publishProgress(R.string.message_download_already_running);
						}
						freeSlots++;
						continue;
					}
				}

				try {
					String enclosureFile = cursor
							.getString(cursor
									.getColumnIndex(DatabaseHelper.ExtendedEpisode.ENCLOSURE_FILE));
					if (enclosureFile != null
							&& new File(new URI(enclosureFile)).isFile()) {
						// the file already exists
						if (params != null) {
							publishProgress(R.string.message_download_episode_already_downloaded);
						}
						freeSlots++;
						continue;
					}
				} catch (URISyntaxException e) {
					// if the URI is invalid, the file can't even exist so we
					// just don't care
				}

				// get necessary information and enqueue download
				long enclosureId = cursor
						.getLong(cursor
								.getColumnIndex(DatabaseHelper.ExtendedEpisode.ENCLOSURE_ID));
				long episodeId = cursor.getLong(cursor
						.getColumnIndex(DatabaseHelper.ExtendedEpisode.ID));
				String title = cursor
						.getString(cursor
								.getColumnIndex(DatabaseHelper.ExtendedEpisode.EPISODE_TITLE));
				String url = cursor
						.getString(cursor
								.getColumnIndex(DatabaseHelper.ExtendedEpisode.ENCLOSURE_URL));
				downloadId = ed.downloadEnclosure(enclosureId, url, title);

				// update enclosure table
				values.clear();
				values.put(DatabaseHelper.Enclosure.DOWNLOAD_ID, downloadId);
				db.update(DatabaseHelper.Enclosure._TABLE, values,
						String.format("%s = ?", DatabaseHelper.Enclosure.ID),
						new String[] { String.valueOf(enclosureId) });

				// update episode table
				values.clear();
				values.put(DatabaseHelper.Episode.STATE,
						DatabaseHelper.Episode.STATE_DOWNLOADING);
				db.update(DatabaseHelper.Episode._TABLE, values,
						String.format("%s = ?", DatabaseHelper.Episode.ID),
						new String[] { String.valueOf(episodeId) });
				if (params != null) {
					publishProgress(R.string.message_download_started);
				}
			}
			cursor.close();

			return null;

		}

		@Override
		protected void onProgressUpdate(Integer... values) {

			Toast.makeText(DownloadService.this, values[0], Toast.LENGTH_SHORT)
					.show();

		}

		@Override
		protected void onPostExecute(Void result) {

			stopSelf();

		}

	}

	private class BatteryChangedReceiver extends BroadcastReceiver {

		private Long[] extraId;

		public BatteryChangedReceiver(Long[] extraId) {
			this.extraId = extraId;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			unregisterReceiver(this);
			phonePlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

			// the charging state is now known so we can start the DownloadTask
			new DownloadTask().execute(extraId);
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

		Long[] id = null;
		long[] extraId = intent.getLongArrayExtra("id");
		if (extraId != null && extraId.length > 0) {
			if (extraId.length != 0) {
				id = new Long[extraId.length];
				for (int i = 0; i < id.length; i++) {
					id[i] = extraId[i];
				}
			}
		}

		// we need to register this broadcast receiver to get the charging state
		registerReceiver(new BatteryChangedReceiver(id), new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		return START_STICKY;

	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;

	}

}
