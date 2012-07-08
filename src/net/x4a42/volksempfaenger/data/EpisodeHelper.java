package net.x4a42.volksempfaenger.data;

import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;

public class EpisodeHelper {

	private static final String EPISODE_WHERE_ID_IN = Episode._ID + " IN (%s)";

	public static void markAsListened(ContentResolver resolver, Uri uri) {
		ContentValues values = new ContentValues();
		values.put(Episode.STATUS, Constants.EPISODE_STATE_LISTENED);
		resolver.update(uri, values, null, null);
	}

	public static void markAsListened(ContentResolver resolver, long id) {
		markAsListened(resolver, ContentUris.withAppendedId(
				VolksempfaengerContentProvider.EPISODE_URI, id));
	}

	public static void markAsListened(ContentResolver resolver, Long... ids) {
		ContentValues values = new ContentValues();
		values.put(Episode.STATUS, Constants.EPISODE_STATE_LISTENED);
		resolver.update(VolksempfaengerContentProvider.EPISODE_URI, values,
				String.format(EPISODE_WHERE_ID_IN, Utils.joinArray(ids, ",")),
				null);
	}

}
