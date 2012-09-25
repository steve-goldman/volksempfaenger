package net.x4a42.volksempfaenger.service;

import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.EpisodeHelper;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;

public class FlattrService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new FlattrEpisodesTask()
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class FlattrEpisodesTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			ContentResolver resolver = getContentResolver();
			Cursor cursor = resolver.query(
					VolksempfaengerContentProvider.EPISODE_URI, new String[] {
							Episode._ID, Episode.FLATTR_URL },
					Episode.FLATTR_STATUS + " = ?", new String[] { String
							.valueOf(Constants.FLATTR_STATE_PENDING) }, null);
			if (cursor.getCount() < 1) {
				cursor.close();
				return null;
			}
			long[] ids = new long[cursor.getCount()];
			boolean[] flattred = new boolean[cursor.getCount()];
			EpisodeCursor episode = new EpisodeCursor(cursor);
			try {
				episode.moveToFirst();
				int i = 0;
				while (!episode.isAfterLast()) {
					ids[i] = episode.getId();
					flattred[i] = flattrUrl(episode.getFlattrUrl());
					i++;
				}

			} finally {
				episode.close();
			}

			// update flattr states
			int count = 0;
			for (int i = 0; i < flattred.length; i++) {
				if (flattred[i]) {
					count++;
				}
			}
			long[] flattredIds = new long[count];
			int j = 0;
			for (int i = 0; i < flattred.length; i++) {
				if (flattred[i]) {
					flattredIds[j] = ids[i];
					j++;
				}
			}
			EpisodeHelper.markAsFlattred(resolver, flattredIds);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			stopSelf();
		}

	}

	private boolean flattrUrl(String url) {
		return false;
	}

}
