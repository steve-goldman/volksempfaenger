package net.x4a42.volksempfaenger.data;

import android.content.ContentValues;

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

	public int updatePodcastDir(ContentValues values,
			String selection, String[] selectionArgs) {
		return update(PODCAST_TABLE, values, selection, selectionArgs);
	}

	public int updatePodcastItem(long id, ContentValues values,
			String selection, String[] selectionArgs) {
		return updatePodcastDir(values, PODCAST_WHERE_ID,
				selectionArray(id));
	}

	// Episode

	public int updateEpisodeDir(ContentValues values,
			String selection, String[] selectionArgs) {
		return update(EPISODE_TABLE, values, selection, selectionArgs);
	}

	public int updateEpisodeItem(long id, ContentValues values,
			String selection, String[] selectionArgs) {
		return updateEpisodeDir(values, EPISODE_WHERE_ID,
				selectionArray(id));
	}

	// Enclosure

	public int updateEnclosureDir(ContentValues values,
			String selection, String[] selectionArgs) {
		return update(ENCLOSURE_TABLE, values, selection, selectionArgs);
	}

	public int updateEnclosureItem(long id, ContentValues values,
			String selection, String[] selectionArgs) {
		return updateEnclosureDir(values, ENCLOSURE_WHERE_ID,
				selectionArray(id));
	}

}
