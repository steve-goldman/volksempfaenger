package net.x4a42.volksempfaenger.event.episodedownload;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

public class EpisodeDownloadEventBroadcasterBuilder
{
    public EpisodeDownloadEventBroadcaster build(Context context)
    {
        EventBus                    eventBus = EventBus.getDefault();
        EpisodeDownloadEventBuilder builder  = new EpisodeDownloadEventBuilder();
        return new EpisodeDownloadEventBroadcaster(eventBus, builder);
    }
}
