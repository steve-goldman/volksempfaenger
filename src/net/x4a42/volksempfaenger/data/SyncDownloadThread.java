package net.x4a42.volksempfaenger.data;

import net.x4a42.volksempfaenger.Log;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

public class SyncDownloadThread implements Runnable {

	private Thread thread = new Thread(this, "SyncDownloadThread");
	private VolksempfaengerContentProvider contentProvider;
	private DatabaseHelper dbHelper;
	private DownloadManager dlManager;

	public SyncDownloadThread(VolksempfaengerContentProvider contentProvider) {
		this.contentProvider = contentProvider;
		this.dbHelper = DatabaseHelper
				.getInstance(contentProvider.getContext());
		this.dlManager = (DownloadManager) contentProvider.getContext()
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
		Log.d(this, "Cursor updated");
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
