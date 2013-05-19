package net.x4a42.volksempfaenger.service;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.Set;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.PreferenceKeys;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.data.Columns;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.misc.NetworkHelper;
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
import android.widget.Toast;

public class DownloadService extends Service {

	private VolksempfaengerApplication app;

	private enum EpisodeCheckResult {
		CONTINUE,
		NEXT
	}

	private class DownloadTask extends AsyncTask<Void, Integer, Integer> {

		private long[] extraIds;
		private boolean forceDownload;

		private static final int ABORT_MOBILE_NETWORK = 1;

		public DownloadTask(long[] extraIds, boolean forceDownload) {
			this.extraIds = extraIds;
			this.forceDownload = forceDownload;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			Log.v(this, "doInBackground()");

			SharedPreferences prefs = app.getSharedPreferences();

			Set<NetworkHelper.NetworkType> networkAllowed = EnumSet.of(NetworkHelper.NetworkType.NETWORK_WIFI);

			Intent batteryIntent = registerReceiver(null, new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED));

			if (!prefs
					.getBoolean(
							PreferenceKeys.DOWNLOAD_WIFI,
							Utils.stringBoolean(getString(R.string.settings_default_download_wifi)))) {
				// downloading is not restricted to WiFi
				networkAllowed.add(NetworkHelper.NetworkType.NETWORK_MOBILE);
			}

			if (!forceDownload) {
				// check if automatic downloads are allowed

				if (checkIfDownloadForbidden(prefs, networkAllowed, batteryIntent)) return null;
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
				String[] projection = {Episode._ID, Episode.TITLE,
						Episode.ENCLOSURE_ID, Episode.ENCLOSURE_URL,
						Episode.DOWNLOAD_ID};
				cursor = getContentResolver().query(
						VolksempfaengerContentProvider.EPISODE_URI, projection,
						selection, null, orderBy);
			}

			EnclosureDownloader ed = new EnclosureDownloader(
					DownloadService.this, forceDownload || networkAllowed.contains(NetworkHelper.NetworkType.NETWORK_WIFI),
					forceDownload || networkAllowed.contains(NetworkHelper.NetworkType.NETWORK_MOBILE));
			DownloadManager dm = ed.getDownloadManager();

			int freeSlots = extraIds == null ? ed.getFreeDownloadSlots()
					: cursor.getCount();

			Log.v(this, String.format(
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
						EpisodeCheckResult result = checkEpisode(downloadId, dmCursor, dm);

						if (result == EpisodeCheckResult.NEXT) {
							freeSlots++;
							continue;
						}
					}
				}

				downloadAndUpdateState(cursor, ed, values);

				if (extraIds != null) {
					publishProgress(R.string.message_download_started);
				}
			}
			cursor.close();
			return null;
		}

		private void downloadAndUpdateState(Cursor cursor, EnclosureDownloader ed, ContentValues values) {
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
		}


		private EpisodeCheckResult checkEpisode(long downloadId, Cursor dmCursor, DownloadManager dm) {
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
							return EpisodeCheckResult.NEXT;
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
					return EpisodeCheckResult.NEXT;
			}

			return EpisodeCheckResult.CONTINUE;
		}

		private boolean checkIfDownloadForbidden(SharedPreferences prefs, Set<NetworkHelper.NetworkType> networkAllowed, Intent batteryIntent) {
			if (!prefs
					.getBoolean(
							PreferenceKeys.DOWNLOAD_AUTO,
							Utils.stringBoolean(getString(R.string.settings_default_download_auto)))) {
				// automatic downloading is disabled
				Log.v(this, "automatic downloading is disabled");
				return true;
			}

			int phonePlugged = batteryIntent.getIntExtra(
					BatteryManager.EXTRA_PLUGGED, -1);

			if (phonePlugged == 0
					&& prefs.getBoolean(
					PreferenceKeys.DOWNLOAD_CHARGING,
					Utils.stringBoolean(getString(R.string.settings_default_download_charging)))) {
				// downloading is only allowed while charging but phone is
				// not plugged in
				Log.v(this, "phone is not plugged in");
				return true;
			}

			Set<NetworkHelper.NetworkType> networkType = NetworkHelper.getNetworkType(app);

			for (NetworkHelper.NetworkType type : networkType) {
				if (networkAllowed.contains(type)) {
					return false;
				}
			}

			// no allowed network connection
			Log.v(this, "network type is not allowed");
			return true;

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Toast.makeText(DownloadService.this, values[0], Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result != null) {
				switch (result) {
					case ABORT_MOBILE_NETWORK:
						Toast.makeText(DownloadService.this,
								R.string.error_download_mobile_disabled,
								Toast.LENGTH_LONG).show();
						break;
				}
			}
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
		Log.v(this, "onStartCommand()");

		long[] extraId = null;
		boolean forceDownload = false;
		if (intent != null) {
			extraId = intent.getLongArrayExtra("id");
			forceDownload = intent.getBooleanExtra("forceDownload", false);
		}

		new DownloadTask(extraId, forceDownload)
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


}
