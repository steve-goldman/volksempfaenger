package net.x4a42.volksempfaenger.service.playback;

import org.greenrobot.eventbus.EventBus;

public class PlaybackEventBroadcasterBuilder
{
    public PlaybackEventBroadcaster build()
    {
        EventBus eventBus = EventBus.getDefault();

        return new PlaybackEventBroadcaster(eventBus);
    }
}
