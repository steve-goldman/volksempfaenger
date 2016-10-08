package net.x4a42.volksempfaenger.receiver.downloadcomplete;

import android.app.DownloadManager;
import android.content.Intent;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownload;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoWrapper;
import net.x4a42.volksempfaenger.downloadmanager.DownloadManagerAdapter;
import net.x4a42.volksempfaenger.event.episodedownload.EpisodeDownloadEvent;
import net.x4a42.volksempfaenger.event.episodedownload.EpisodeDownloadEventBroadcaster;

class DownloadCompleteReceiverProxy
{
    private final EpisodeDownloadEventBroadcaster eventBroadcaster;
    private final DownloadManagerAdapter          downloadManagerAdapter;
    private final EpisodeDownloadDaoWrapper       episodeDownloadDao;

    public DownloadCompleteReceiverProxy(EpisodeDownloadEventBroadcaster eventBroadcaster,
                                         DownloadManagerAdapter          downloadManagerAdapter,
                                         EpisodeDownloadDaoWrapper       episodeDownloadDao)
    {
        this.eventBroadcaster       = eventBroadcaster;
        this.downloadManagerAdapter = downloadManagerAdapter;
        this.episodeDownloadDao     = episodeDownloadDao;
    }

    public void onReceive(Intent intent)
    {
        if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction()))
        {
            return;
        }

        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

        if (!episodeDownloadDao.hasDownloadId(downloadId))
        {
            // user-initiated cancels (i.e. removing from playlist) would trigger this
            return;
        }

        EpisodeDownload episodeDownload = episodeDownloadDao.getByDownloadId(downloadId);

        if (downloadManagerAdapter.isSuccess(downloadId))
        {
            // TODO: update duration in database using MediaMetadataRetriever

            sendSuccessEvent(episodeDownload.getEpisode());
        }
        else
        {
            sendFailedEvent(episodeDownload.getEpisode());
        }
    }

    private void sendSuccessEvent(Episode episode)
    {
        eventBroadcaster.broadcast(episode, EpisodeDownloadEvent.Action.DOWNLOADED);
    }

    private void sendFailedEvent(Episode episode)
    {
        eventBroadcaster.broadcast(episode, EpisodeDownloadEvent.Action.FAILED);
    }
}
