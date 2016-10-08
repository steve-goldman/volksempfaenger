package net.x4a42.volksempfaenger.service.playlistdownload;

import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownload;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItem;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;
import net.x4a42.volksempfaenger.downloadmanager.DownloadManagerAdapter;
import net.x4a42.volksempfaenger.event.episodedownload.EpisodeDownloadEvent;
import net.x4a42.volksempfaenger.event.episodedownload.EpisodeDownloadEventBroadcaster;
import net.x4a42.volksempfaenger.misc.ConnectivityStatus;

class PlaylistDownloadTaskProxy
{
    private final Preferences                     preferences;
    private final PlaylistItemDaoWrapper          playlistItemDao;
    private final EpisodeDownloadDaoWrapper       episodeDownloadDao;
    private final DownloadManagerAdapter          downloadManagerAdapter;
    private final ConnectivityStatus              connectivityStatus;
    private final EpisodeDownloadEventBroadcaster eventBroadcaster;

    public PlaylistDownloadTaskProxy(Preferences                     preferences,
                                     PlaylistItemDaoWrapper          playlistItemDao,
                                     EpisodeDownloadDaoWrapper       episodeDownloadDao,
                                     DownloadManagerAdapter          downloadManagerAdapter,
                                     ConnectivityStatus              connectivityStatus,
                                     EpisodeDownloadEventBroadcaster eventBroadcaster)
    {
        this.preferences            = preferences;
        this.playlistItemDao        = playlistItemDao;
        this.episodeDownloadDao     = episodeDownloadDao;
        this.downloadManagerAdapter = downloadManagerAdapter;
        this.connectivityStatus     = connectivityStatus;
        this.eventBroadcaster       = eventBroadcaster;
    }

    public void doInBackground()
    {
        adjustForConnectivity();
        addTopN();
        removeRest();
    }

    private void adjustForConnectivity()
    {
        for (EpisodeDownload episodeDownload : episodeDownloadDao.getAll())
        {
            long downloadId = episodeDownload.getDownloadId();

            // reset running downloads when wifi and not connected to wifi
            if (preferences.downloadWifiOnly()
                    && !connectivityStatus.isWifiConnected()
                    && downloadManagerAdapter.isRunning(downloadId))
            {
                remove(episodeDownload);
            }
            // reset downloads that are paused waiting for wifi when they don't need to be
            else if (!preferences.downloadWifiOnly() && downloadManagerAdapter.isPausedForWifi(downloadId))
            {
                remove(episodeDownload);
            }
        }
    }

    private void addTopN()
    {
        for (PlaylistItem playlistItem : playlistItemDao.getFirstN(preferences.getDownloadedQueueCount()))
        {
            Episode episode = playlistItem.getEpisode();
            if (!episodeDownloadDao.hasEpisode(episode))
            {
                long downloadId = downloadManagerAdapter.enqueue(episode);
                episodeDownloadDao.insert(episode, downloadId);
            }
        }
    }

    private void removeRest()
    {
        for (EpisodeDownload episodeDownload : episodeDownloadDao.getAll())
        {
            Episode episode = episodeDownload.getEpisode();
            if (playlistItemDao.hasEpisode(episode))
            {
                PlaylistItem playlistItem = playlistItemDao.getByEpisode(episode);
                if (playlistItem.getPosition() > preferences.getDownloadedQueueCount())
                {
                    remove(episodeDownload);
                }
            }
            else
            {
                remove(episodeDownload);
            }
        }
    }

    private void remove(EpisodeDownload episodeDownload)
    {
        downloadManagerAdapter.cancel(episodeDownload.getDownloadId());
        episodeDownloadDao.delete(episodeDownload);
        eventBroadcaster.broadcast(episodeDownload.getEpisode(), EpisodeDownloadEvent.Action.REMOVED);
    }
}
