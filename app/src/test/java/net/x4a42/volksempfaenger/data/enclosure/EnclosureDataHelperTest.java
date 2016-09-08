package net.x4a42.volksempfaenger.data.enclosure;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import net.x4a42.volksempfaenger.data.EnclosureCursor;
import net.x4a42.volksempfaenger.data.EnclosureCursorFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class EnclosureDataHelperTest
{
    ContentResolver        contentResolver   = Mockito.mock(ContentResolver.class);
    Cursor                 cursor            = Mockito.mock(Cursor.class);
    EnclosureCursorFactory cursorFactory     = Mockito.mock(EnclosureCursorFactory.class);
    EnclosureCursor        enclosureCursor   = Mockito.mock(EnclosureCursor.class);
    EnclosureMetadata      enclosureMetadata = Mockito.mock(EnclosureMetadata.class);
    long                   episodeId         = 10;
    EnclosureDataHelper    enclosureDataHelper;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(contentResolver.query(Mockito.any(Uri.class),
                                           Mockito.any(String[].class),
                                           Mockito.anyString(),
                                           Mockito.any(String[].class),
                                           Mockito.anyString()))
               .thenReturn(cursor);

        Mockito.when(cursorFactory.create(cursor)).thenReturn(enclosureCursor);

        enclosureDataHelper = new EnclosureDataHelper(contentResolver,
                                                      cursorFactory,
                                                      enclosureMetadata);
    }

    @Test
    public void testGetForEpisode() throws Exception
    {
        EnclosureCursor resultCursor = enclosureDataHelper.getForEpisode(episodeId);
        Assert.assertEquals(enclosureCursor, resultCursor);
    }
}