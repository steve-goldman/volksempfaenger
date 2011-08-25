package net.x4a42.volksempfaenger.receiver;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

public class DownloadCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		DownloadManager dm = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);

		String action = intent.getAction();
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			long downloadId = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			Query query = new Query();
			query.setFilterById(downloadId);
			Cursor c = dm.query(query);
			if (c.moveToFirst()) {
				int columnIndex = c
						.getColumnIndex(DownloadManager.COLUMN_STATUS);
				if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
					String uriString = c.getString(c
							.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
					Log.d(getClass().getSimpleName(),
							String.format("Download finished: %s", uriString));
				}
			}
		}
	}

}
