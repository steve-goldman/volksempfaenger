package net.x4a42.volksempfaenger.service;

import net.x4a42.volksempfaenger.data.DatabaseHelper;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class DownloadService extends Service {

	private DatabaseHelper dbHelper;

	enum NetworkType {
		NONE, WIFI, MOBILE, UNKNOWN
	}

	private class DownloadTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo netInfo = cm.getActiveNetworkInfo();

			NetworkType network;
			if (netInfo == null
					|| netInfo.getState() != NetworkInfo.State.CONNECTED) {
				network = NetworkType.NONE;
			} else {
				switch (netInfo.getType()) {
				case ConnectivityManager.TYPE_WIFI:
					network = NetworkType.WIFI;
					break;
				case ConnectivityManager.TYPE_MOBILE:
					network = NetworkType.MOBILE;
				default:
					network = NetworkType.UNKNOWN;
					break;
				}
			}

			return null;

		}

		@Override
		protected void onPostExecute(Void result) {

			stopSelf();

		}

	}

	@Override
	public void onCreate() {

		super.onCreate();
		dbHelper = new DatabaseHelper(this);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.d(getClass().getSimpleName(), "onStartCommand()");
		new DownloadTask().execute();
		return START_STICKY;

	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;

	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		dbHelper.close();

	}

}