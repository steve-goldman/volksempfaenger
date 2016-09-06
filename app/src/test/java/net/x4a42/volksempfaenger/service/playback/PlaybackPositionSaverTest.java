package net.x4a42.volksempfaenger.service.playback;

import android.content.ContentResolver;
import android.content.ContentValues;

import net.x4a42.volksempfaenger.data.Columns;
import net.x4a42.volksempfaenger.misc.ContentValuesFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PlaybackPositionSaverTest
{
    ContentResolver       contentResolver = Mockito.mock(ContentResolver.class);
    ContentValues         contentValues   = Mockito.mock(ContentValues.class);
    PlaybackPositionSaver saver;

    ContentValuesFactory  contentValuesFactory = new ContentValuesFactory()
    {
        @Override
        public ContentValues create()
        {
            return contentValues;
        }
    };

    @Before
    public void setUp() throws Exception
    {
        saver = new PlaybackPositionSaver(contentResolver, contentValuesFactory);
    }

    @Test
    public void testSave() throws Exception
    {
        PlaybackItem playbackItem = Mockito.mock(PlaybackItem.class);
        int          position     = 10;

        saver.save(playbackItem, position);

        Mockito.verify(contentValues).put(Columns.Episode.DURATION_LISTENED, position);
        Mockito.verify(contentResolver).update(playbackItem.getUriTime(), contentValues, null, null);
    }
}