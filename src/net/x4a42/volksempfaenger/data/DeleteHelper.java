package net.x4a42.volksempfaenger.data;


public class DeleteHelper extends ContentProviderHelper {

	protected DeleteHelper(DatabaseHelper dbHelper) {
		super(dbHelper);
	}

	private int delete(String table, String selection, String[] selectionArgs) {
		return getWritableDatabase().delete(table, selection, selectionArgs);
	}

	public int deletePodcastDir(String selection, String[] selectionArgs) {
		return delete(PODCAST_TABLE, selection, selectionArgs);
	}

	public int deletePodcastItem(long id, String selection,
			String[] selectionArgs) {
		return deletePodcastDir(PODCAST_WHERE_ID, selectionArray(id));
	}

	public int deleteEpisodeDir(String selection, String[] selectionArgs) {
		return delete(EPISODE_TABLE, selection, selectionArgs);
	}

	public int deleteEpisodeItem(long id, String selection,
			String[] selectionArgs) {
		return deleteEpisodeDir(EPISODE_WHERE_ID, selectionArray(id));
	}

	public int deleteEnclosureDir(String selection, String[] selectionArgs) {
		return delete(ENCLOSURE_TABLE, selection, selectionArgs);
	}

	public int deleteEnclosureItem(long id, String selection,
			String[] selectionArgs) {
		return deleteEnclosureDir(ENCLOSURE_WHERE_ID, selectionArray(id));
	}

}
