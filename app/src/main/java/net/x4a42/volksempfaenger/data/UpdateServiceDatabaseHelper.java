package net.x4a42.volksempfaenger.data;

import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.service.UpdateService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class UpdateServiceDatabaseHelper {

	private UpdateService service;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase database;
	private ContentResolver resolver;
	private boolean inTransaction;

	public UpdateServiceDatabaseHelper(UpdateService service) {
		this.service = service;
		this.dbHelper = DatabaseHelper.getInstance(service);
		this.database = dbHelper.getWritableDatabase();
		this.resolver = service.getContentResolver();
	}

	public void startTransaction() {
		inTransaction = true;
		database.beginTransaction();
	}

	public void ensureTransaction() {
		if (!inTransaction) {
			startTransaction();
		}
	}

	public void finishTransaction() {
		inTransaction = false;
		database.setTransactionSuccessful();
		database.endTransaction();
	}

	public void updateFeed(Feed feed) {
		ContentValues values = new ContentValues();
		values.put(Podcast.TITLE, feed.title);
		values.put(Podcast.DESCRIPTION, feed.description);
		values.put(Podcast.WEBSITE, feed.website);
		values.put(Podcast.LAST_UPDATE, System.currentTimeMillis() / 1000);
		// values.put(Podcast.FEED, ...);

		// TODO
	}

	// TODO

}
