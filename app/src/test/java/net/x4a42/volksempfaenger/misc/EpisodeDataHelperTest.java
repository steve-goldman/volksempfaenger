package net.x4a42.volksempfaenger.misc;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import net.x4a42.volksempfaenger.data.Columns;
import net.x4a42.volksempfaenger.data.Constants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class EpisodeDataHelperTest
{
    ContentResolver      contentResolver      = Mockito.mock(ContentResolver.class);
    ContentValuesFactory contentValuesFactory = Mockito.mock(ContentValuesFactory.class);
    ContentValues        contentValues        = Mockito.mock(ContentValues.class);
    Uri                  episodeUri           = Mockito.mock(Uri.class);
    EpisodeDataHelper    episodeDataHelper;

    @Before
    public void setUp() throws Exception
    {
        episodeDataHelper = new EpisodeDataHelper(contentResolver, contentValuesFactory);
        Mockito.when(contentValuesFactory.create()).thenReturn(contentValues);
    }

    @Test
    public void markAsListened() throws Exception
    {
        episodeDataHelper.markAsListened(episodeUri);

        Mockito.verify(contentValues).put(Columns.Episode.STATUS, Constants.EPISODE_STATE_LISTENING);
        Mockito.verify(contentResolver).update(episodeUri, contentValues, null, null);
    }
}