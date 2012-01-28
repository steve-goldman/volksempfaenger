package net.x4a42.volksempfaenger.data;

import net.x4a42.volksempfaenger.data.Columns.Enclosure;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Columns.Podcast;
import android.content.ContentUris;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ContentProviderHelper {

	protected static final String PODCAST_TABLE = DatabaseHelper.TABLE_PODCAST;
	protected static final String EPISODE_TABLE = DatabaseHelper.TABLE_EPISODE;
	protected static final String ENCLOSURE_TABLE = DatabaseHelper.TABLE_ENCLOSURE;

	protected static final String PODCAST_WHERE_ID = PODCAST_TABLE + "."
			+ Podcast._ID + "=?";
	protected static final String EPISODE_WHERE_ID = EPISODE_TABLE + "."
			+ Episode._ID + "=?";
	protected static final String ENCLOSURE_WHERE_ID = ENCLOSURE_TABLE + "."
			+ Enclosure._ID + "=?";

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

	protected String[] selectionArray(long id) {
		return new String[] { String.valueOf(id) };
	}

	protected String[] selectionArray(Uri uri) {
		return selectionArray(ContentUris.parseId(uri));
	}

}
