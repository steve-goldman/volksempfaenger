package net.x4a42.volksempfaenger.receiver;

import net.x4a42.volksempfaenger.data.DatabaseHelper;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;

public class DownloadCompleteReceiver extends BroadcastReceiver {

	private class DownloadCompleteTask extends AsyncTask<Void, Void, Void> {

		private Context context;
		private Intent intent;
		private DatabaseHelper dbHelper;

		public DownloadCompleteTask(Context context, Intent intent) {
			this.context = context;
			this.intent = intent;
			dbHelper = DatabaseHelper.getInstance(context);
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

				SQLiteDatabase db = dbHelper.getWritableDatabase();
				ContentValues episodeValues = new ContentValues();
				ContentValues enclosureValues = new ContentValues();

				Cursor cur = db.query(DatabaseHelper.Enclosure._TABLE,
						new String[] { DatabaseHelper.Enclosure.EPISODE },
						String.format("%s = ?",
								DatabaseHelper.Enclosure.DOWNLOAD_ID),
						new String[] { String.valueOf(downloadId) }, null,
						null, null);

				if (!cur.moveToFirst()) {
					// somehow the downloaded enclosure doesn't exist anymore
					return null;
				}

				long episodeId = cur.getLong(cur
						.getColumnIndex(DatabaseHelper.Enclosure.EPISODE));

				switch (c.getInt(c
						.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
				case DownloadManager.STATUS_SUCCESSFUL:

					String localUri = c.getString(c
							.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

					MediaMetadataRetriever mmr = new MediaMetadataRetriever();
					mmr.setDataSource(context, Uri.parse(localUri));

					enclosureValues
							.put(DatabaseHelper.Enclosure.FILE, localUri);
					enclosureValues
							.put(DatabaseHelper.Enclosure.MIME,
									c.getString(c
											.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE)));
					enclosureValues
							.put(DatabaseHelper.Enclosure.SIZE,
									c.getLong(c
											.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)));
					enclosureValues
							.put(DatabaseHelper.Enclosure.DURATION_TOTAL,
									mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
					episodeValues.put(DatabaseHelper.Episode.STATE,
							DatabaseHelper.Episode.STATE_READY);

					break;
				case DownloadManager.STATUS_FAILED:
					episodeValues.put(DatabaseHelper.Episode.STATE,
							DatabaseHelper.Episode.STATE_NEW);
					break;
				}

				if (enclosureValues.size() > 0) {
					db.update(DatabaseHelper.Enclosure._TABLE, enclosureValues,
							String.format("%s = ?",
									DatabaseHelper.Enclosure.DOWNLOAD_ID),
							new String[] { String.valueOf(downloadId) });
				}
				if (episodeValues.size() > 0) {
					db.update(DatabaseHelper.Episode._TABLE, episodeValues,
							String.format("%s = ?", DatabaseHelper.Episode.ID),
							new String[] { String.valueOf(episodeId) });
				}
			}
			return null;

		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE
				.equals(intent.getAction())) {
			// we do not care about any other intents
			return;
		}

		new DownloadCompleteTask(context, intent).execute();

	}

}
