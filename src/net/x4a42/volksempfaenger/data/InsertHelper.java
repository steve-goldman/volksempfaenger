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

	private long insert(String table, ContentValues values) {
		long id;
		try {
			id = getWritableDatabase().insertOrThrow(PODCAST_TABLE, null,
					values);
		} catch (SQLiteConstraintException e) {
			throw new Error.DuplicateException();
		} catch (SQLException e) {
			throw new Error.InsertException();
		}
		if (id == -1) {
			// This might happen if the row was not inserted but no error
			// occurred. Sounds pretty strange but this is Android.
			throw new Error.InsertException();
		}
		return id;
	}

	public Uri insertPodcast(Uri uri, ContentValues values) {
		long id = insert(PODCAST_TABLE, values);
		return ContentUris.withAppendedId(
				VolksempfaengerContentProvider.PODCAST_URI, id);
	}

	public Uri insertEpisode(Uri uri, ContentValues values) {
		long id = insert(EPISODE_TABLE, values);
		return ContentUris.withAppendedId(
				VolksempfaengerContentProvider.EPISODE_URI, id);
	}

	public Uri insertEnclosure(Uri uri, ContentValues values) {
		long id = insert(ENCLOSURE_TABLE, values);
		return ContentUris.withAppendedId(
				VolksempfaengerContentProvider.ENCLOSURE_URI, id);
	}

}
