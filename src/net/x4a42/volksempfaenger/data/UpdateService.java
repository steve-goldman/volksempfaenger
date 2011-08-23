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

		lastRun = System.currentTimeMillis();

		Log.d(getClass().getSimpleName(), "onCreate()");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);

		Log.d(getClass().getSimpleName(), "onStart()");

		stopSelf();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.d(getClass().getSimpleName(), "onStartCommand()");

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(getClass().getSimpleName(), "onBind()");

		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(getClass().getSimpleName(), "onUnbind()");

		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		super.onRebind(intent);

		Log.d(getClass().getSimpleName(), "onRebind()");
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