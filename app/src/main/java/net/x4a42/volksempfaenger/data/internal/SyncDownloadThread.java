package net.x4a42.volksempfaenger.data.internal;

import net.x4a42.volksempfaenger.BuildConfig;
import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.DatabaseHelper;
import net.x4a42.volksempfaenger.data.ExtendedCursorWrapper;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.ContentResolver;
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
	private ContentResolver contentResolver;

	public SyncDownloadThread(Context context, DatabaseHelper helper) {
		dbHelper = helper;
		dlManager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		contentResolver = context.getContentResolver();
	}

	public void start() {
		thread.start();
	}

	public void stop() {
		thread.interrupt();
	}

	@Override
	public synchronized void run() {
		try {
			while (true) {
				Cursor cursor = dlManager.query(new DownloadManager.Query());
				if (cursor == null) {
					Thread.sleep(1000);
					continue;
				}
				cursor.registerContentObserver(mContentObserver);
				onCursorUpdated(cursor);
				wait(120000);
				cursor.close();
			}
		} catch (InterruptedException e) {
			return;
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

		contentResolver.notifyChange(
				VolksempfaengerContentProvider.EPISODE_URI, mContentObserver);

		// just some debugging output. maybe remove this later
		if (BuildConfig.DEBUG) {
			ExtendedCursorWrapper qc = new ExtendedCursorWrapper(db.query(
					TABLE, null, null, null, null, null, null));
			while (qc.moveToNext()) {
				Log.v(this, qc.rowToString());
			}
			qc.close();
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
