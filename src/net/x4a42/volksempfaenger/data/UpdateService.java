package net.x4a42.volksempfaenger.data;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {

	private static long lastRun = 0;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		Log.d(getClass().getSimpleName(), "onCreate()");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.d(getClass().getSimpleName(), "onStartCommand()");

		lastRun = System.currentTimeMillis();

		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		Log.d(getClass().getSimpleName(), "onDestroy()");
	}

	public static long getLastRun() {
		return lastRun;
	}

}