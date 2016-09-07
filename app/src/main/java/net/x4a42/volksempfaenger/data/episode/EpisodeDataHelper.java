package net.x4a42.volksempfaenger.data.episode;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import net.x4a42.volksempfaenger.data.Columns;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.misc.ContentValuesFactory;

public class EpisodeDataHelper
{
    private final ContentResolver      contentResolver;
    private final ContentValuesFactory contentValuesFactory;
    private final DownloadManager      downloadManager;

    public EpisodeDataHelper(ContentResolver      contentResolver,
                             ContentValuesFactory contentValuesFactory,
                             DownloadManager      downloadManager)
    {
        this.contentResolver      = contentResolver;
        this.contentValuesFactory = contentValuesFactory;
        this.downloadManager      = downloadManager;
    }

    public void markListening(Uri episodeUri)
    {
        updateStatus(episodeUri, Constants.EPISODE_STATE_LISTENING);
    }

    public void markListened(Uri episodeUri)
    {
        updateStatus(episodeUri, Constants.EPISODE_STATE_LISTENED);
    }

    public void markNew(Uri episodeUri)
    {
        updateStatus(episodeUri, Constants.EPISODE_STATE_NEW);
    }

    public void delete(Uri episodeUri, long episodeId)
    {
        downloadManager.remove(episodeId);
        ContentValues values = contentValuesFactory.create();
        values.putNull(Columns.Episode.DOWNLOAD_ID);
        values.put(Columns.Episode.STATUS, Constants.EPISODE_STATE_LISTENED);
        contentResolver.update(episodeUri, values, null, null);

    }

    public void setEnclosure(Uri episodeUri, long enclosureId)
    {
        ContentValues values = contentValuesFactory.create();
        values.put(Columns.Episode.ENCLOSURE_ID, enclosureId);
        contentResolver.update(episodeUri, values, null, null);
    }

    private void updateStatus(Uri episodeUri, int status)
    {
        ContentValues values = contentValuesFactory.create();
        values.put(Columns.Episode.STATUS, status);
        contentResolver.update(episodeUri, values, null, null);
    }

}
