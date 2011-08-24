package net.x4a42.volksempfaenger.service;

import net.x4a42.volksempfaenger.data.DatabaseHelper;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

public class DownloadService extends Service {

	private DatabaseHelper dbHelper;
	private int phonePlugged;

	private static final int FLAG_CHARGING = 1; // 0001
	private static final int FLAG_WIFI = 2; // 0010
	private static final int FLAG_MOBILE = 4; // 0100
	private static final int FLAGS_NETWORK = 6; // 0110

	private class DownloadTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			// get network state
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			int status = 0;
			if (netInfo != null
					&& netInfo.getState() == NetworkInfo.State.CONNECTED) {
				switch (netInfo.getType()) {
				case ConnectivityManager.TYPE_WIFI:
					status |= FLAG_WIFI;
					break;
				case ConnectivityManager.TYPE_MOBILE:
					status |= FLAG_MOBILE;
				}
			}

			// get charging state
			if (phonePlugged > 0) {
				status |= FLAG_CHARGING;
			}

			// TODO
			Log.d(getClass().getSimpleName(), "status=" + status);

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
		dbHelper = new DatabaseHelper(this);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.d(getClass().getSimpleName(), "onStartCommand()");

		// we need to register this broadcast receiver to get the charging state
		registerReceiver(new BatteryChangedReceiver(), new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

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