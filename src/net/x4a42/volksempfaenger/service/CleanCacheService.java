package net.x4a42.volksempfaenger.service;

import java.io.File;
import java.io.FilenameFilter;

import net.x4a42.volksempfaenger.Constants;
import net.x4a42.volksempfaenger.Log;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

public class CleanCacheService extends Service {

	private class CleanCacheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			final File cacheDir = getExternalCacheDir();
			final File images = new File(cacheDir, "images");
			// 3 days
			final long minTime = System.currentTimeMillis() - 3 * 24 * 60 * 60
					* 1000;
			final File[] imageFiles = images.listFiles();
			final File[] errorReportFiles = cacheDir
					.listFiles(new FilenameFilter() {

						@Override
						public boolean accept(File dir, String filename) {
							if (filename
									.startsWith(Constants.ERROR_REPORT_PREFIX)) {
								return true;
							} else {
								return false;
							}
						}
					});
			try {
				if (imageFiles != null) {
					CleanCacheService.removeOldFiles(imageFiles, minTime);
				}
				if (errorReportFiles != null) {
					CleanCacheService.removeOldFiles(errorReportFiles, minTime);
				}
			} catch (Exception e) {
				/*
				 * do not crash everything because removing temporary files
				 * fails. probably just a temporary problem
				 */
				Log.w(this, e);
				e.printStackTrace();
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

	public static void removeOldFiles(File[] files, long minLastModified) {
		for (File f : files) {
			if (f.lastModified() < minLastModified) {
				f.delete();
			}
		}
	}

}
