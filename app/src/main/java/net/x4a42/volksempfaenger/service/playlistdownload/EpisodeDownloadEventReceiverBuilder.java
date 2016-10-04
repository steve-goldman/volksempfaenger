package net.x4a42.volksempfaenger.service.playlistdownload;

import org.greenrobot.eventbus.EventBus;

public class EpisodeDownloadEventReceiverBuilder
{
    public EpisodeDownloadEventReceiver build()
    {
        return new EpisodeDownloadEventReceiver(EventBus.getDefault());
    }
}
