package net.x4a42.volksempfaenger.data;

import android.net.Uri;

public class DeleteHelper extends ContentProviderHelper {

	protected DeleteHelper(DatabaseHelper dbHelper) {
		super(dbHelper);
	}

	private int delete(String table, String selection, String[] selectionArgs) {
		return getWritableDatabase().delete(table, selection, selectionArgs);
	}

	public int deletePodcastDir(Uri uri, String selection,
			String[] selectionArgs) {
		return delete(PODCAST_TABLE, selection, selectionArgs);
	}

	public int deletePodcastItem(Uri uri, String selection,
			String[] selectionArgs) {
		return deletePodcastDir(null, PODCAST_WHERE_ID, selectionArray(uri));
	}

	public int deleteEpisodeDir(Uri uri, String selection,
			String[] selectionArgs) {
		return delete(EPISODE_TABLE, selection, selectionArgs);
	}

	public int deleteEpisodeItem(Uri uri, String selection,
			String[] selectionArgs) {
		return deleteEpisodeDir(null, EPISODE_WHERE_ID, selectionArray(uri));
	}

	public int deleteEnclosureDir(Uri uri, String selection,
			String[] selectionArgs) {
		return delete(ENCLOSURE_TABLE, selection, selectionArgs);
	}

	public int deleteEnclosureItem(Uri uri, String selection,
			String[] selectionArgs) {
		return deleteEnclosureDir(null, ENCLOSURE_WHERE_ID, selectionArray(uri));
	}

}
