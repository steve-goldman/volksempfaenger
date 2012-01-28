package net.x4a42.volksempfaenger.data;

import android.database.sqlite.SQLiteDatabase;

public class ContentProviderHelper {
	
	protected static final String PODCAST_TABLE = DatabaseHelper.TABLE_PODCAST;
	protected static final String EPISODE_TABLE = DatabaseHelper.TABLE_EPISODE;
	protected static final String ENCLOSURE_TABLE = DatabaseHelper.TABLE_ENCLOSURE;

	private DatabaseHelper dbHelper;
	
	protected ContentProviderHelper(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}
	
	protected final SQLiteDatabase getReadableDatabase() {
		return dbHelper.getReadableDatabase();
	}
	
	protected final SQLiteDatabase getWritableDatabase() {
		return dbHelper.getWritableDatabase();
	}
	
}
