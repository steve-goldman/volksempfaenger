package net.x4a42.volksempfaenger.receiver;

import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.service.DownloadService;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;

public class DownloadCompleteReceiver extends BroadcastReceiver {

	private static final String WHERE_DOWNLOAD_ID = Episode.DOWNLOAD_ID + "=?";

	private class DownloadCompleteTask extends AsyncTask<Void, Void, Void> {

		private Context context;
		private Intent intent;

		public DownloadCompleteTask(Context context, Intent intent) {
			this.context = context;
			this.intent = intent;
		}

		@Override
		protected Void doInBackground(Void... params) {
			DownloadManager dm = (DownloadManager) context
					.getSystemService(Context.DOWNLOAD_SERVICE);
			long downloadId = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, 0);

			Query query = new Query();
			query.setFilterById(downloadId);
			Cursor c = dm.query(query);
			if (c.moveToFirst()) {
				ContentValues values = new ContentValues();

				switch (c.getInt(c
						.getColumnIndex(DownloadManager.COLUMN_STATUS))) {

				case DownloadManager.STATUS_SUCCESSFUL:
					String localUri = c.getString(c
							.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
					MediaMetadataRetriever mmr = new MediaMetadataRetriever();
					mmr.setDataSource(context, Uri.parse(localUri));
					String duration = mmr
							.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
					values.put(Episode.DURATION_TOTAL, duration);
					values.put(Episode.STATUS, Constants.EPISODE_STATE_READY);
					break;

				case DownloadManager.STATUS_FAILED:
					values.put(Episode.STATUS, Constants.EPISODE_STATE_NEW);
					break;

				}

				if (values.size() > 0) {
					context.getContentResolver().update(
							VolksempfaengerContentProvider.EPISODE_URI, values,
							WHERE_DOWNLOAD_ID,
							new String[] { String.valueOf(downloadId) });
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// start DownloadService to start automatic downloads if there are
			// new free slots
			Intent downloadServiceIntent = new Intent(context,
					DownloadService.class);
			context.startService(downloadServiceIntent);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE
				.equals(intent.getAction())) {
			// we do not care about any other intents
			return;
		}

		new DownloadCompleteTask(context, intent)
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}
