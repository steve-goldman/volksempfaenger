package net.x4a42.volksempfaenger.data;

import android.content.ContentValues;
import android.net.Uri;

public class UpdateHelper extends ContentProviderHelper {

	protected UpdateHelper(DatabaseHelper dbHelper) {
		super(dbHelper);
	}

	private int update(String table, ContentValues values, String selection,
			String[] selectionArgs) {
		return getWritableDatabase().update(table, values, selection,
				selectionArgs);
	}

	// Podcast

	public int updatePodcastDir(Uri uri, ContentValues values,
			String selection, String[] selectionArgs) {
		return update(PODCAST_TABLE, values, selection, selectionArgs);
	}

	public int updatePodcastItem(Uri uri, ContentValues values,
			String selection, String[] selectionArgs) {
		return updatePodcastDir(uri, values, PODCAST_WHERE_ID,
				selectionArray(uri));
	}

	// Episode

	public int updateEpisodeDir(Uri uri, ContentValues values,
			String selection, String[] selectionArgs) {
		return update(EPISODE_TABLE, values, selection, selectionArgs);
	}

	public int updateEpisodeItem(Uri uri, ContentValues values,
			String selection, String[] selectionArgs) {
		return updateEpisodeDir(uri, values, EPISODE_WHERE_ID,
				selectionArray(uri));
	}

	// Enclosure

	public int updateEnclosureDir(Uri uri, ContentValues values,
			String selection, String[] selectionArgs) {
		return update(ENCLOSURE_TABLE, values, selection, selectionArgs);
	}

	public int updateEnclosureItem(Uri uri, ContentValues values,
			String selection, String[] selectionArgs) {
		return updateEnclosureDir(uri, values, ENCLOSURE_WHERE_ID,
				selectionArray(uri));
	}

}
