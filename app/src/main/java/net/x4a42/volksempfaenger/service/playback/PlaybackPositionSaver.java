package net.x4a42.volksempfaenger.service.playback;

import android.content.ContentResolver;
import android.content.ContentValues;

import net.x4a42.volksempfaenger.data.Columns;
import net.x4a42.volksempfaenger.misc.ContentValuesFactory;

public class PlaybackPositionSaver
{
    private final ContentResolver      contentResolver;
    private final ContentValuesFactory contentValuesFactory;

    public PlaybackPositionSaver(ContentResolver      contentResolver,
                                 ContentValuesFactory contentValuesFactory)
    {
        this.contentResolver      = contentResolver;
        this.contentValuesFactory = contentValuesFactory;
    }

    public void save(PlaybackItem playbackItem, int position)
    {
        ContentValues values = contentValuesFactory.create();
        values.put(Columns.Episode.DURATION_LISTENED, position);
        contentResolver.update(playbackItem.getUriTime(), values, null, null);
    }
}
