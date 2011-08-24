package net.x4a42.volksempfaenger.service;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class CleanCacheService extends Service {

	private class CleanCacheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			File cache = getExternalCacheDir();

			File images = new File(cache, "images");

			long minTime = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000;

			File[] imageFiles = images.listFiles();
			if (imageFiles != null) {
				for (File f : imageFiles) {
					if (f.lastModified() < minTime) {
						f.delete();
					}
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
	public int onStartCommand(Intent intent, int flags, int startId) {

		new CleanCacheTask().execute();

		return START_STICKY;

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}