package net.x4a42.volksempfaenger.misc;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import net.x4a42.volksempfaenger.data.Columns;
import net.x4a42.volksempfaenger.data.Constants;

public class EpisodeDataHelper
{
    private final ContentResolver      contentResolver;
    private final ContentValuesFactory contentValuesFactory;

    public EpisodeDataHelper(ContentResolver      contentResolver,
                             ContentValuesFactory contentValuesFactory)
    {
        this.contentResolver      = contentResolver;
        this.contentValuesFactory = contentValuesFactory;
    }

    public void markAsListened(Uri episodeUri)
    {
        ContentValues values = contentValuesFactory.create();
        values.put(Columns.Episode.STATUS, Constants.EPISODE_STATE_LISTENING);
        contentResolver.update(episodeUri, values, null, null);
    }
}
