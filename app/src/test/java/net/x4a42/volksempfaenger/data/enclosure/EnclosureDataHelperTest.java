package net.x4a42.volksempfaenger.data.enclosure;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import net.x4a42.volksempfaenger.data.EnclosureCursor;
import net.x4a42.volksempfaenger.data.EnclosureCursorFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EnclosureDataHelperTest
{
    @Mock ContentResolver        contentResolver;
    @Mock Cursor                 cursor;
    @Mock EnclosureCursorFactory cursorFactory;
    @Mock EnclosureCursor        enclosureCursor;
    @Mock EnclosureMetadata      enclosureMetadata;
    long                         episodeId         = 10;
    EnclosureDataHelper          enclosureDataHelper;

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
