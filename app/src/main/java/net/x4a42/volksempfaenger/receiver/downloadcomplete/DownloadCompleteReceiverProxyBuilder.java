package net.x4a42.volksempfaenger.receiver.downloadcomplete;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoWrapper;
import net.x4a42.volksempfaenger.downloadmanager.DownloadManagerAdapter;
import net.x4a42.volksempfaenger.downloadmanager.DownloadManagerAdapterBuilder;
import net.x4a42.volksempfaenger.event.episodedownload.EpisodeDownloadEventBuilder;

import org.greenrobot.eventbus.EventBus;

class DownloadCompleteReceiverProxyBuilder
{
    public DownloadCompleteReceiverProxy build(Context context)
    {
        EventBus eventBus = EventBus.getDefault();

        DownloadManagerAdapter downloadManagerAdapter
                = new DownloadManagerAdapterBuilder().build(context);

        EpisodeDownloadDaoWrapper episodeDownloadDao
                = new EpisodeDownloadDaoBuilder().build(context);

        EpisodeDownloadEventBuilder eventBuilder = new EpisodeDownloadEventBuilder();

        return new DownloadCompleteReceiverProxy(eventBus,
                                                 downloadManagerAdapter,
                                                 episodeDownloadDao,
                                                 eventBuilder);
    }
}
