package net.x4a42.volksempfaenger.service.playlistdownload;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

class EpisodeDownloadEventBroadcasterBuilder
{
    public EpisodeDownloadEventBroadcaster build(Context context)
    {
        EventBus                    eventBus = EventBus.getDefault();
        EpisodeDownloadEventBuilder builder  = new EpisodeDownloadEventBuilder();
        return new EpisodeDownloadEventBroadcaster(eventBus, builder);
    }
}
