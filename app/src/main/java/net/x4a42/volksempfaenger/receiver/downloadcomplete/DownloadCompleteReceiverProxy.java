package net.x4a42.volksempfaenger.receiver.downloadcomplete;

import android.app.DownloadManager;
import android.content.Intent;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownload;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoWrapper;
import net.x4a42.volksempfaenger.downloadmanager.DownloadManagerAdapter;
import net.x4a42.volksempfaenger.event.playlistdownload.EpisodeDownloadEvent;
import net.x4a42.volksempfaenger.event.playlistdownload.EpisodeDownloadEventBuilder;

import org.greenrobot.eventbus.EventBus;

class DownloadCompleteReceiverProxy
{
    private final EventBus                    eventBus;
    private final DownloadManagerAdapter      downloadManagerAdapter;
    private final EpisodeDownloadDaoWrapper   episodeDownloadDao;
    private final EpisodeDownloadEventBuilder eventBuilder;

    public DownloadCompleteReceiverProxy(EventBus                    eventBus,
                                         DownloadManagerAdapter      downloadManagerAdapter,
                                         EpisodeDownloadDaoWrapper   episodeDownloadDao,
                                         EpisodeDownloadEventBuilder eventBuilder)
    {
        this.eventBus               = eventBus;
        this.downloadManagerAdapter = downloadManagerAdapter;
        this.episodeDownloadDao     = episodeDownloadDao;
        this.eventBuilder           = eventBuilder;
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
        eventBus.post(eventBuilder.build(episode, EpisodeDownloadEvent.Action.DOWNLOADED));
    }

    private void sendFailedEvent(Episode episode)
    {
        eventBus.post(eventBuilder.build(episode, EpisodeDownloadEvent.Action.FAILED));
    }
}
