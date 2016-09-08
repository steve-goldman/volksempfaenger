package net.x4a42.volksempfaenger.service.playback;

import org.greenrobot.eventbus.EventBus;

class PlaybackEventBroadcaster
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
