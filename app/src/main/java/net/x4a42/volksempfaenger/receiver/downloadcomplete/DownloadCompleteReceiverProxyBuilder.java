package net.x4a42.volksempfaenger.receiver.downloadcomplete;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoWrapper;
import net.x4a42.volksempfaenger.downloadmanager.DownloadManagerAdapter;
import net.x4a42.volksempfaenger.downloadmanager.DownloadManagerAdapterBuilder;
import net.x4a42.volksempfaenger.event.episodedownload.EpisodeDownloadEventBroadcaster;
import net.x4a42.volksempfaenger.event.episodedownload.EpisodeDownloadEventBroadcasterBuilder;

class DownloadCompleteReceiverProxyBuilder
{
    public DownloadCompleteReceiverProxy build(Context context)
    {
        DownloadManagerAdapter downloadManagerAdapter
                = new DownloadManagerAdapterBuilder().build(context);

        EpisodeDownloadDaoWrapper episodeDownloadDao
                = new EpisodeDownloadDaoBuilder().build(context);

        EpisodeDownloadEventBroadcaster eventBroadcaster
                = new EpisodeDownloadEventBroadcasterBuilder().build(context);

        return new DownloadCompleteReceiverProxy(eventBroadcaster,
                                                 downloadManagerAdapter,
                                                 episodeDownloadDao);
    }
}
