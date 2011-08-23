package net.x4a42.volksempfaenger.data;

import net.x4a42.volksempfaenger.Utils;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class UpdateService extends Service {

	private static long lastRun = 0;

	private DbHelper dbHelper;

	private class UpdateTask extends AsyncTask<Long, Long, Void> {

		@Override
		protected Void doInBackground(Long... params) {

			Cursor cursor;

			if (params == null) {
				// All podcasts get updated as no IDs were passed
				lastRun = System.currentTimeMillis();

				cursor = dbHelper.getReadableDatabase().query(
						DbHelper.Podcast._TABLE, null, null, null, null, null,
						null);
			} else {
				cursor = dbHelper.getReadableDatabase().query(
						DbHelper.Podcast._TABLE,
						null,
						String.format("%s in (%s)", DbHelper.Podcast.ID,
								Utils.joinArray(params, ",")), null, null,
						null, null);
			}

			while (cursor.moveToNext()) {
				long id = cursor.getLong(cursor
						.getColumnIndex(DbHelper.Podcast.ID));
				publishProgress(id);
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Long... values) {
			Toast.makeText(UpdateService.this, "Updated " + values[0],
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPostExecute(Void result) {
			stopSelf();
		}

	}

	@Override
	public void onCreate() {
		dbHelper = new DbHelper(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Long[] id = null;
		Bundle extras = intent.getExtras();
		if (extras != null) {
			long[] extraId = extras.getLongArray("id");
			if (extraId.length != 0) {
				id = new Long[extraId.length];
				for (int i = 0; i < id.length; i++) {
					id[i] = extraId[i];
				}
			}
		}

		new UpdateTask().execute(id);

		return START_NOT_STICKY;

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		dbHelper.close();
	}

	public static long getLastRun() {
		return lastRun;
	}

}