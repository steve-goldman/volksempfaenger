package net.x4a42.volksempfaenger.data;

import net.x4a42.volksempfaenger.BuildConfig;
import net.x4a42.volksempfaenger.Log;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class SyncDownloadThread implements Runnable {

	private static final String TABLE = "DownloadManager.download";

	private Thread thread = new Thread(this, "SyncDownloadThread");
	private DatabaseHelper dbHelper;
	private DownloadManager dlManager;

	public SyncDownloadThread(VolksempfaengerContentProvider contentProvider) {
		Context context = contentProvider.getContext();
		dbHelper = DatabaseHelper.getInstance(context);
		dlManager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
	}

	public void start() {
		thread.start();
	}

	public void stop() {
		thread.interrupt();
	}

	@Override
	public synchronized void run() {
		while (true) {
			Cursor cursor = dlManager.query(new DownloadManager.Query());
			cursor.registerContentObserver(mContentObserver);

			onCursorUpdated(cursor);

			try {
				wait();
			} catch (InterruptedException e) {
				return;
			}

			cursor.close();
		}
	}

	private void onCursorUpdated(Cursor cursor) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();

		final int columnIdIndex = cursor
				.getColumnIndex(DownloadManager.COLUMN_ID);
		final int columnLocalFilenameIndex = cursor
				.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
		final int columnMediaproviderUriIndex = cursor
				.getColumnIndex(DownloadManager.COLUMN_MEDIAPROVIDER_URI);
		final int columnTitleIndex = cursor
				.getColumnIndex(DownloadManager.COLUMN_TITLE);
		final int columnDescriptionIndex = cursor
				.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION);
		final int columnUriIndex = cursor
				.getColumnIndex(DownloadManager.COLUMN_URI);
		final int columnStatusIndex = cursor
				.getColumnIndex(DownloadManager.COLUMN_STATUS);
		final int columnMediaTypeIndex = cursor
				.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE);
		final int columnTotalSizeBytesIndex = cursor
				.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
		final int columnLastModifiedTimestampIndex = cursor
				.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP);
		final int columnBytesDownloadedSoFarIndex = cursor
				.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
		final int columnLocalUriIndex = cursor
				.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
		final int columnReasonIndex = cursor
				.getColumnIndex(DownloadManager.COLUMN_REASON);

		db.beginTransaction();

		while (cursor.moveToNext()) {
			values.clear();

			values.put(DownloadManager.COLUMN_ID, cursor.getLong(columnIdIndex));
			values.put(DownloadManager.COLUMN_LOCAL_FILENAME,
					cursor.getString(columnLocalFilenameIndex));
			values.put(DownloadManager.COLUMN_MEDIAPROVIDER_URI,
					cursor.getString(columnMediaproviderUriIndex));
			values.put(DownloadManager.COLUMN_TITLE,
					cursor.getString(columnTitleIndex));
			values.put(DownloadManager.COLUMN_DESCRIPTION,
					cursor.getString(columnDescriptionIndex));
			values.put(DownloadManager.COLUMN_URI,
					cursor.getString(columnUriIndex));
			values.put(DownloadManager.COLUMN_STATUS,
					cursor.getInt(columnStatusIndex));
			values.put(DownloadManager.COLUMN_MEDIA_TYPE,
					cursor.getString(columnMediaTypeIndex));
			values.put(DownloadManager.COLUMN_TOTAL_SIZE_BYTES,
					cursor.getLong(columnTotalSizeBytesIndex));
			values.put(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP,
					cursor.getLong(columnLastModifiedTimestampIndex));
			values.put(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR,
					cursor.getLong(columnBytesDownloadedSoFarIndex));
			values.put(DownloadManager.COLUMN_LOCAL_URI,
					cursor.getString(columnLocalUriIndex));
			values.put(DownloadManager.COLUMN_REASON,
					cursor.getInt(columnReasonIndex));

			db.insertWithOnConflict(TABLE, null, values,
					SQLiteDatabase.CONFLICT_REPLACE);

			db.yieldIfContendedSafely();
		}

		db.setTransactionSuccessful();
		db.endTransaction();

		// just some debugging output. maybe remove this later
		if (BuildConfig.DEBUG) {
			Cursor qc = db.query(TABLE, null, null, null, null, null, null);
			while (qc.moveToNext()) {
				String line = "";
				for (int i = 0; i < qc.getColumnCount(); i++) {
					line += qc.getColumnName(i);
					line += "=";
					line += qc.getString(i);
					line += " ";
				}
				Log.d(this, line);
			}
		}
	}

	private ContentObserver mContentObserver = new ContentObserver(null) {

		@TargetApi(16)
		@Override
		public void onChange(boolean selfChange) {
			onChange(selfChange, null);
		}

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			synchronized (SyncDownloadThread.this) {
				SyncDownloadThread.this.notify();
			}
		}

	};

}
