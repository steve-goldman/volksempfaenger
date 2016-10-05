package net.x4a42.volksempfaenger.event.playback;

import org.greenrobot.eventbus.EventBus;

public class PlaybackEventBroadcaster
{
    private final EventBus eventBus;

    public PlaybackEventBroadcaster(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }

    public void broadcast(PlaybackEvent playbackEvent)
    {
        eventBus.post(playbackEvent);
    }
}
