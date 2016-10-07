package net.x4a42.volksempfaenger.event.playback;

import org.greenrobot.eventbus.EventBus;

public class PlaybackEventReceiverBuilder
{
    public PlaybackEventReceiver build()
    {
        return new PlaybackEventReceiver(EventBus.getDefault());
    }
}
