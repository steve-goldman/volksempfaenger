package net.x4a42.volksempfaenger.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;

public class InsertHelper extends ContentProviderHelper {

	protected InsertHelper(DatabaseHelper dbHelper) {
		super(dbHelper);
	}

	public Uri insertPodcast(Uri uri, ContentValues values) {
		long id;
		try {
			id = getWritableDatabase().insertOrThrow(PODCAST_TABLE, null,
					values);
		} catch (SQLException e) {
			if (e instanceof SQLiteConstraintException) {
				throw new Error.DuplicateException();
			} else {
				throw new Error.InsertException();
			}
		}
		if (id == -1) {
			throw new Error.InsertException();
		}
		return ContentUris.withAppendedId(
				VolksempfaengerContentProvider.PODCAST_URI, id);
	}

	public Uri insertEpisode(Uri uri, ContentValues values) {
		long id;
		try {
			id = getWritableDatabase().insertOrThrow(EPISODE_TABLE, null,
					values);
		} catch (SQLException e) {
			if (e instanceof SQLiteConstraintException) {
				throw new Error.DuplicateException();
			} else {
				throw new Error.InsertException();
			}
		}
		if (id == -1) {
			throw new Error.InsertException();
		}
		return ContentUris.withAppendedId(
				VolksempfaengerContentProvider.EPISODE_URI, id);
	}

	public Uri insertEnclosure(Uri uri, ContentValues values) {
		long id;
		try {
			id = getWritableDatabase().insertOrThrow(ENCLOSURE_TABLE, null,
					values);
		} catch (SQLException e) {
			if (e instanceof SQLiteConstraintException) {
				throw new Error.DuplicateException();
			} else {
				throw new Error.InsertException();
			}
		}
		if (id == -1) {
			throw new Error.InsertException();
		}
		return ContentUris.withAppendedId(
				VolksempfaengerContentProvider.ENCLOSURE_URI, id);
	}

}
