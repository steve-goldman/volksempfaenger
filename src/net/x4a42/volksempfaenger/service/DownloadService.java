package net.x4a42.volksempfaenger.service;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import net.x4a42.volksempfaenger.PreferenceKeys;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.net.EnclosureDownloader;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.Service;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.IBinder;
import net.x4a42.volksempfaenger.Log;
import android.widget.Toast;

public class DownloadService extends Service {


	private static final int NETWORK_WIFI = 1;
	private static final int NETWORK_MOBILE = 2;

	private VolksempfaengerApplication app;

	private class DownloadTask extends AsyncTask<Void, Integer, Void> {

		private long[] extraIds;

		public DownloadTask(long[] extraIds) {
			this.extraIds = extraIds;
		}

		@Override
		protected Void doInBackground(Void... params) {
			Log.d(this, "doInBackground()");

			SharedPreferences prefs = app.getSharedPreferences();

			int networkAllowed = 0;

			// if automatic downloading is enabled, downloading via WiFi is
			// enabled
			networkAllowed |= NETWORK_WIFI;

			Intent batteryIntent = registerReceiver(null, new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED));

			if (!prefs
					.getBoolean(
							PreferenceKeys.DOWNLOAD_WIFI,
							Utils.stringBoolean(getString(R.string.settings_default_download_wifi)))) {
				// downloading is not restricted to WiFi
				networkAllowed |= NETWORK_MOBILE;
			}

			if (extraIds == null) {
				// check if automatic downloads are allowed

				if (!prefs
						.getBoolean(
								PreferenceKeys.DOWNLOAD_AUTO,
								Utils.stringBoolean(getString(R.string.settings_default_download_auto)))) {
					// automatic downloading is disabled
					Log.d(this,
							"automatic downloading is disabled");
					return null;
				}

				int phonePlugged = batteryIntent.getIntExtra(
						BatteryManager.EXTRA_PLUGGED, -1);

				if (phonePlugged == 0
						&& prefs.getBoolean(
								PreferenceKeys.DOWNLOAD_CHARGING,
								Utils.stringBoolean(getString(R.string.settings_default_download_charging)))) {
					// downloading is only allowed while charging but phone is
					// not plugged in
					Log.d(this, "phone is not plugged in");
					return null;
				}

				int networkType = 0;

				// get network state
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

				if (!cm.getBackgroundDataSetting()) {
					// background data is disabled
					Log.d(this,
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

				if ((networkType & networkAllowed) == 0) {
					// no allowed network connection
					Log.d(this,
							"network type is not allowed");
					return null;
				}

			}

			// here we can finally start the downloads

			String selection = "enclosure.url IS NOT NULL";
			String orderBy = null;

			if (extraIds == null) {
				selection += " AND episode.status = "
						+ Constants.EPISODE_STATE_NEW;

				orderBy = String.format("%s DESC", Episode.DATE);
			} else {
				selection += " AND episode._id IN ("
						+ Utils.joinArray(extraIds, ",") + ")";
			}

			Cursor cursor;
			{
				String[] projection = { Episode._ID, Episode.TITLE,
						Episode.ENCLOSURE_ID, Episode.ENCLOSURE_URL,
						Episode.DOWNLOAD_ID };
				cursor = getContentResolver().query(
						VolksempfaengerContentProvider.EPISODE_URI, projection,
						selection, null, orderBy);
			}

			EnclosureDownloader ed = new EnclosureDownloader(
					DownloadService.this, (networkAllowed & NETWORK_WIFI) != 0,
					(networkAllowed & NETWORK_MOBILE) != 0);
			DownloadManager dm = ed.getDownloadManager();

			int freeSlots = extraIds == null ? ed.getFreeDownloadSlots()
					: cursor.getCount();

			Log.d(this, String.format(
					"starting downloads inQueue:%d freeSlots:%d",
					cursor.getCount(), freeSlots));

			ContentValues values = new ContentValues();
			while (cursor.moveToNext() && freeSlots-- > 0) {
				int downloadIdCol = cursor.getColumnIndex(Episode.DOWNLOAD_ID);
				if (!cursor.isNull(downloadIdCol)) {
					// TODO: Find out why this code is *here*
					long downloadId = cursor.getLong(downloadIdCol);
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
									// the file was successfully downloaded and
									// does
									// still exist
									if (extraIds != null) {
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
								// this should never ever happen but just in
								// case
								// we'll handle it like a failed download (next
								// case)
							}
						case DownloadManager.STATUS_FAILED:
							// remove the download so that we can start a new
							// one
							dm.remove(downloadId);
							break;

						case DownloadManager.STATUS_PENDING:
						case DownloadManager.STATUS_RUNNING:
						case DownloadManager.STATUS_PAUSED:
							// the download is already running
							if (extraIds != null) {
								publishProgress(R.string.message_download_already_running);
							}
							freeSlots++;
							continue;
						}
					}
				}

				// get necessary information and enqueue download
				long enclosureId = cursor.getLong(cursor
						.getColumnIndex(Episode.ENCLOSURE_ID));
				long episodeId = cursor.getLong(cursor
						.getColumnIndex(Episode._ID));
				String title = cursor.getString(cursor
						.getColumnIndex(Episode.TITLE));
				String url = cursor.getString(cursor
						.getColumnIndex(Episode.ENCLOSURE_URL));
				long downloadId = ed.downloadEnclosure(enclosureId, url, title);

				// Update episode
				values.clear();
				values.put(Episode.DOWNLOAD_ID, downloadId);
				values.put(Episode.STATUS, Constants.EPISODE_STATE_DOWNLOADING);
				getContentResolver().update(
						ContentUris.withAppendedId(
								VolksempfaengerContentProvider.EPISODE_URI,
								episodeId), values, null, null);

				if (extraIds != null) {
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

	@Override
	public void onCreate() {
		super.onCreate();
		app = (VolksempfaengerApplication) getApplication();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(this, "onStartCommand()");

		long[] extraId = intent.getLongArrayExtra("id");
		new DownloadTask(extraId).execute();

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
