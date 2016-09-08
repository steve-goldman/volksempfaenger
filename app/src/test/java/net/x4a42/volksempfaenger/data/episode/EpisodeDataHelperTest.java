package net.x4a42.volksempfaenger.data.episode;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import net.x4a42.volksempfaenger.data.Columns;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.misc.ContentValuesFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class EpisodeDataHelperTest
{
    ContentResolver      contentResolver      = Mockito.mock(ContentResolver.class);
    ContentValuesFactory contentValuesFactory = Mockito.mock(ContentValuesFactory.class);
    ContentValues        contentValues        = Mockito.mock(ContentValues.class);
    DownloadManager      downloadManager      = Mockito.mock(DownloadManager.class);
    Uri                  episodeUri           = Mockito.mock(Uri.class);
    long                 episodeId            = 10;
    long                 enclosureId          = 20;
    int                  duration             = 300;
    EpisodeDataHelper    episodeDataHelper;

    @Before
    public void setUp() throws Exception
    {
        episodeDataHelper = new EpisodeDataHelper(contentResolver,
                                                  contentValuesFactory,
                                                  downloadManager);
        Mockito.when(contentValuesFactory.create()).thenReturn(contentValues);
    }

    @Test
    public void markListening() throws Exception
    {
        episodeDataHelper.markListening(episodeUri);

        Mockito.verify(contentValues)
               .put(Columns.Episode.STATUS, Constants.EPISODE_STATE_LISTENING);
        Mockito.verify(contentResolver).update(episodeUri, contentValues, null, null);
    }

    @Test
    public void markListened() throws Exception
    {
        episodeDataHelper.markListened(episodeUri);

        Mockito.verify(contentValues).put(Columns.Episode.STATUS, Constants.EPISODE_STATE_LISTENED);
        Mockito.verify(contentResolver).update(episodeUri, contentValues, null, null);
    }

    @Test
    public void markNew() throws Exception
    {
        episodeDataHelper.markNew(episodeUri);

        Mockito.verify(contentValues).put(Columns.Episode.STATUS, Constants.EPISODE_STATE_NEW);
        Mockito.verify(contentResolver).update(episodeUri, contentValues, null, null);
    }

    @Test
    public void delete() throws Exception
    {
        episodeDataHelper.delete(episodeUri, episodeId);

        Mockito.verify(downloadManager).remove(episodeId);
        Mockito.verify(contentValues).putNull(Columns.Episode.DOWNLOAD_ID);
        Mockito.verify(contentValues).put(Columns.Episode.STATUS, Constants.EPISODE_STATE_LISTENED);
        Mockito.verify(contentResolver).update(episodeUri, contentValues, null, null);
    }

    @Test
    public void setEnclosure() throws Exception
    {
        episodeDataHelper.setEnclosure(episodeUri, enclosureId);

        Mockito.verify(contentValues).put(Columns.Episode.ENCLOSURE_ID, enclosureId);
        Mockito.verify(contentResolver).update(episodeUri, contentValues, null, null);
    }

    @Test
    public void setDurationListened() throws Exception
    {
        episodeDataHelper.setDurationListened(episodeUri, duration);

        Mockito.verify(contentValues).put(Columns.Episode.DURATION_LISTENED, duration);
        Mockito.verify(contentResolver).update(episodeUri, contentValues, null, null);
    }
}
