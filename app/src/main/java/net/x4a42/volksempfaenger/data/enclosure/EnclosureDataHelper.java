package net.x4a42.volksempfaenger.data.enclosure;

import android.content.ContentResolver;
import android.database.Cursor;

import net.x4a42.volksempfaenger.data.Columns;
import net.x4a42.volksempfaenger.data.EnclosureCursor;
import net.x4a42.volksempfaenger.data.EnclosureCursorFactory;

public class EnclosureDataHelper
{
    private static final String[]        Projection     =
            {
                Columns.Enclosure._ID,
                Columns.Enclosure.URL
            };

    private static final String          WhereEpisodeId = Columns.Enclosure.EPISODE_ID + "=?";

    private final ContentResolver        contentResolver;
    private final EnclosureCursorFactory cursorFactory;
    private final EnclosureMetadata      enclosureMetadata;

    public EnclosureDataHelper(ContentResolver        contentResolver,
                               EnclosureCursorFactory cursorFactory,
                               EnclosureMetadata      enclosureMetadata)
    {
        this.contentResolver   = contentResolver;
        this.cursorFactory     = cursorFactory;
        this.enclosureMetadata = enclosureMetadata;
    }

    public EnclosureCursor getForEpisode(long episodeId)
    {
        return cursorFactory.create(getCursor(episodeId));
    }

    private Cursor getCursor(long episodeId)
    {
        return contentResolver.query(
                enclosureMetadata.getEnclosureUri(),
                Projection,
                WhereEpisodeId,
                new String[] { String.valueOf(episodeId) },
                null);
    }
}
