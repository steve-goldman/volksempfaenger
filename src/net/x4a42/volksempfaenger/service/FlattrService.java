package net.x4a42.volksempfaenger.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;

import net.x4a42.volksempfaenger.PreferenceKeys;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.EpisodeHelper;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.net.Downloader;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.JsonWriter;

public class FlattrService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String auth = ((VolksempfaengerApplication) getApplication())
				.getSharedPreferences().getString(
						PreferenceKeys.FLATTR_ACCESS_TOKEN, null);
		if (auth != null) {
			new FlattrEpisodesTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, auth);
		} else {
			stopSelf();
		}
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class FlattrEpisodesTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			String auth = params[0];
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
				for (int i = 0; i < cursor.getCount(); i++) {
					ids[i] = episode.getId();
					flattred[i] = flattrUrl(auth, episode.getFlattrUrl());
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

	private boolean flattrUrl(String auth, String url) {
		Downloader downloader = new Downloader(this);
		try {
			HttpURLConnection connection = downloader
					.getConnection("https://api.flattr.com/rest/v2/flattr");
			try {
				connection.setRequestProperty("Content-Type",
						"application/json");
				connection
						.setRequestProperty("Authorization", "Bearer " + auth);
				connection.setDoOutput(true);

				StringWriter bodyWriter = new StringWriter();
				JsonWriter json = new JsonWriter(bodyWriter);
				try {
					json.beginObject();
					json.name("url");
					json.value(url);
					json.endObject();
				} finally {
					json.close();
				}

				String body = bodyWriter.toString();
				connection.setFixedLengthStreamingMode(body.length());
				OutputStreamWriter bodyStreamWriter = new OutputStreamWriter(
						connection.getOutputStream());
				try {
					bodyStreamWriter.write(body);
				} finally {
					bodyStreamWriter.close();
				}
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					return true;
				} else {

					BufferedReader reader = new BufferedReader(
							new InputStreamReader(connection.getErrorStream()));
					try {
						String error = Utils
								.getJsonStringValue(reader, "error");
						if (error == null) {
							return false;
						}
						if (error.equals("flattr_once")
								|| error.equals("flattr_owner")) {
							return true;
						} else {
							return false;
						}
					} finally {
						reader.close();
					}
				}

			} finally {
				connection.disconnect();
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
