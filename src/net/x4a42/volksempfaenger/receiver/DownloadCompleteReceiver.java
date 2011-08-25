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
import android.util.Log;

public class DownloadCompleteReceiver extends BroadcastReceiver {

	private Context context;
	private Intent intent;

	private class DownloadCompleteTask extends AsyncTask<Void, Void, Void> {

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
				// for (int i = 0; i < c.getColumnCount(); i++) {
				// Log.d(getClass().getSimpleName(),
				// String.format("%s: %s", c.getColumnName(i),
				// c.getString(i)));
				// }

				DatabaseHelper dbHelper = new DatabaseHelper(context);
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				ContentValues values = new ContentValues();

				switch (c.getInt(c
						.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
				case DownloadManager.STATUS_SUCCESSFUL:

					String localUri = c.getString(c
							.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

					MediaMetadataRetriever mmr = new MediaMetadataRetriever();
					mmr.setDataSource(context, Uri.parse(localUri));

					values.put(DatabaseHelper.Enclosure.DOWNLOAD_ID,
							(Long) null);
					values.put(DatabaseHelper.Enclosure.FILE, localUri);
					values.put(DatabaseHelper.Enclosure.MIME, c.getString(c
							.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE)));
					values.put(
							DatabaseHelper.Enclosure.SIZE,
							c.getLong(c
									.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)));
					values.put(
							DatabaseHelper.Enclosure.DURATION_TOTAL,
							mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
					values.put(DatabaseHelper.Enclosure.STATE,
							DatabaseHelper.Enclosure.STATE_DOWNLOADED);

					break;
				case DownloadManager.STATUS_FAILED:
					values.put(DatabaseHelper.Enclosure.DOWNLOAD_ID,
							(Long) null);
					values.put(DatabaseHelper.Enclosure.STATE,
							DatabaseHelper.Enclosure.STATE_NEW);
					break;
				}

				db.update(DatabaseHelper.Enclosure._TABLE, values,
						String.format("%s = ?",
								DatabaseHelper.Enclosure.DOWNLOAD_ID),
						new String[] { String.valueOf(downloadId) });
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

		this.context = context;
		this.intent = intent;

		new DownloadCompleteTask().execute();

	}

}
