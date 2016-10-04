package net.x4a42.volksempfaenger.service.playlistdownload;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;

import org.greenrobot.eventbus.EventBus;

class EpisodeDownloadEventBroadcaster
{
    private final EventBus                    eventBus;
    private final EpisodeDownloadEventBuilder builder;

    public EpisodeDownloadEventBroadcaster(EventBus                    eventBus,
                                           EpisodeDownloadEventBuilder builder)
    {
        this.eventBus = eventBus;
        this.builder  = builder;
    }

    public void broadcast(Episode episode, EpisodeDownloadEvent.Action action)
    {
        eventBus.post(builder.build(episode, action));
    }
}
