package net.x4a42.volksempfaenger.data;

import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class EpisodeHelper {

	private static final String EPISODE_WHERE_ID_IN = Episode._ID + " IN (%s)";
	private static final String EPISODE_DOWNLOAD_NOT_NULL = Episode.DOWNLOAD_ID
			+ " IS NOT NULL";
	private static final String[] DOWNLOAD_ID_PROJECTION = { Episode.DOWNLOAD_ID };
	private static final String EPISODE_FLATTRABLE = Episode.FLATTR_STATUS
			+ " = " + Constants.FLATTR_STATE_NEW;
	private static final ContentValues MARK_AS_LISTENED_VALUES = new ContentValues();
	private static final ContentValues MARK_AS_NEW_VALUES = new ContentValues();
	private static final ContentValues REMOVE_DOWNLOAD_VALUES = new ContentValues();
	private static final ContentValues FLATTR_VALUES = new ContentValues();

	static {
		MARK_AS_LISTENED_VALUES.put(Episode.STATUS,
				Constants.EPISODE_STATE_LISTENED);

		MARK_AS_NEW_VALUES.put(Episode.STATUS, Constants.EPISODE_STATE_READY);

		REMOVE_DOWNLOAD_VALUES.putNull(Episode.DOWNLOAD_ID);
		REMOVE_DOWNLOAD_VALUES.put(Episode.STATUS,
				Constants.EPISODE_STATE_LISTENED);
		FLATTR_VALUES
				.put(Episode.FLATTR_STATUS, Constants.FLATTR_STATE_PENDING);
	}

	public static void markAsListened(ContentResolver resolver, Uri uri) {
		resolver.update(uri, MARK_AS_LISTENED_VALUES, null, null);
	}

	public static void markAsListened(ContentResolver resolver, long id) {
		markAsListened(resolver, ContentUris.withAppendedId(
				VolksempfaengerContentProvider.EPISODE_URI, id));
	}

	public static void markAsListened(ContentResolver resolver, long... ids) {
		resolver.update(VolksempfaengerContentProvider.EPISODE_URI,
				MARK_AS_LISTENED_VALUES,
				String.format(EPISODE_WHERE_ID_IN, Utils.joinArray(ids, ",")),
				null);
	}

	public static void markAsNew(ContentResolver resolver, Uri uri) {
		resolver.update(uri, MARK_AS_NEW_VALUES, null, null);
	}

	public static void markAsNew(ContentResolver resolver, long id) {
		markAsNew(resolver, ContentUris.withAppendedId(
				VolksempfaengerContentProvider.EPISODE_URI, id));
	}

	public static void markAsNew(ContentResolver resolver, long... ids) {
		resolver.update(VolksempfaengerContentProvider.EPISODE_URI,
				MARK_AS_NEW_VALUES,
				String.format(EPISODE_WHERE_ID_IN, Utils.joinArray(ids, ",")),
				null);
	}

	public static void deleteDownload(ContentResolver resolver,
			DownloadManager dlManager, Uri uri) {

		// query the download id
		Cursor cursor = resolver.query(uri, DOWNLOAD_ID_PROJECTION,
				EPISODE_DOWNLOAD_NOT_NULL, null, null);

		if (!cursor.moveToFirst()) {
			// the episode does not exist or has not been downloaded
			return;
		}

		// remove from download manager
		dlManager.remove(cursor.getLong(0));

		// update database
		resolver.update(uri, REMOVE_DOWNLOAD_VALUES, null, null);

	}

	public static void deleteDownload(ContentResolver resolver,
			DownloadManager dlManager, long id) {
		deleteDownload(resolver, dlManager, ContentUris.withAppendedId(
				VolksempfaengerContentProvider.EPISODE_URI, id));
	}

	public static void deleteDownload(ContentResolver resolver,
			DownloadManager dlManager, long... ids) {

		// query all affected download ids
		Cursor cursor = resolver
				.query(VolksempfaengerContentProvider.EPISODE_URI,
						DOWNLOAD_ID_PROJECTION,
						String.format(EPISODE_WHERE_ID_IN + " AND "
								+ EPISODE_DOWNLOAD_NOT_NULL,
								Utils.joinArray(ids, ",")), null, null);

		int count = cursor.getCount();

		if (count == 0) {
			// there are no downloadas to delete
			return;
		}

		// move them from the cursor to a long[] array
		long[] downloadIds = new long[count];
		for (int i = 0; cursor.moveToNext(); i++) {
			downloadIds[i] = cursor.getLong(0);
		}

		// remove them from download manager (this also removes the downloaded
		// files)
		dlManager.remove(downloadIds);

		// update the episodes
		resolver.update(VolksempfaengerContentProvider.EPISODE_URI,
				REMOVE_DOWNLOAD_VALUES,
				String.format(EPISODE_WHERE_ID_IN, Utils.joinArray(ids, ",")),
				null);

	}

	public static boolean canMarkAsListened(int status) {
		if (status < Constants.EPISODE_STATE_LISTENED) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean canMarkAsNew(int status) {
		if (status > Constants.EPISODE_STATE_READY) {
			return true;
		} else {
			return false;
		}
	}

	public static void flattr(ContentResolver resolver, Uri uri) {
		resolver.update(uri, FLATTR_VALUES, EPISODE_FLATTRABLE, null);
	}

	public static void flattr(ContentResolver resolver, long id) {
		resolver.update(VolksempfaengerContentProvider.EPISODE_URI,
				FLATTR_VALUES, EPISODE_FLATTRABLE + " AND " + Episode._ID
						+ " = ?", new String[] { String.valueOf(id) });
	}
}
