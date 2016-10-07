package net.x4a42.volksempfaenger.downloadmanager;

import android.app.DownloadManager;
import android.database.Cursor;
import android.net.Uri;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodePathProvider;

public class DownloadManagerAdapter
{
    private final DownloadManager               downloadManager;
    private final DownloadManagerRequestBuilder requestBuilder;
    private final DownloadManagerQueryProvider  queryProvider;
    private final EpisodePathProvider           pathProvider;
    private final UriBuilder                    uriBuilder;
    private final Preferences                   preferences;

    public DownloadManagerAdapter(DownloadManager               downloadManager,
                                  DownloadManagerRequestBuilder requestBuilder,
                                  DownloadManagerQueryProvider  queryProvider,
                                  EpisodePathProvider           pathProvider,
                                  UriBuilder                    uriBuilder,
                                  Preferences                   preferences)
    {
        this.downloadManager = downloadManager;
        this.requestBuilder  = requestBuilder;
        this.queryProvider   = queryProvider;
        this.pathProvider    = pathProvider;
        this.uriBuilder      = uriBuilder;
        this.preferences     = preferences;
    }

    public boolean isPausedForWifi(long downloadId)
    {
        return getStatus(downloadId) == DownloadManager.STATUS_PAUSED
                && getReason(downloadId) == DownloadManager.PAUSED_WAITING_FOR_NETWORK;
    }

    public boolean isRunning(long downloadId)
    {
        return getStatus(downloadId) == DownloadManager.STATUS_RUNNING;
    }

    public boolean isSuccess(long downloadId)
    {
        return getStatus(downloadId) == DownloadManager.STATUS_SUCCESSFUL;
    }

    public boolean isFailed(long downloadId)
    {
        return getStatus(downloadId) == DownloadManager.STATUS_FAILED;
    }

    private int getReason(long downloadId)
    {
        return getInt(downloadId, DownloadManager.COLUMN_REASON);
    }

    public long enqueue(Episode episode)
    {
        String                  url     = episode.getEnclosures().get(0).getUrl();
        Uri                     source  = uriBuilder.build(url);
        DownloadManager.Request request = requestBuilder.build(source);

        request.setDestinationUri(pathProvider.getEpisodeUri(episode));
        request.setAllowedNetworkTypes(getAllowedNetworkTypes());
        request.setTitle(episode.getPodcast().getTitle());
        request.setDescription(episode.getTitle());

        return downloadManager.enqueue(request);
    }

    public void cancel(long downloadId)
    {
        downloadManager.remove(downloadId);
    }

    private int getAllowedNetworkTypes()
    {
        return preferences.downloadWifiOnly() ? DownloadManager.Request.NETWORK_WIFI :
                DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE;
    }

    private int getStatus(long downloadId)
    {
        return getInt(downloadId, DownloadManager.COLUMN_STATUS);
    }

    private int getInt(long downloadId, String columnName)
    {
        DownloadManager.Query query = queryProvider.get();
        query.setFilterById(downloadId);

        int value = 0;
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst())
        {
            value = cursor.getInt(cursor.getColumnIndex(columnName));
            cursor.close();
        }
        else
        {
            Log.d(this, String.format("no row for downloadId:%d", downloadId));
        }

        return value;
    }
}
