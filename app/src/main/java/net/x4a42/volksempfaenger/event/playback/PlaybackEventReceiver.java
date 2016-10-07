package net.x4a42.volksempfaenger.event.playback;

/*
    This is the companion class to PlaybackEventBroadcaster. It listens for PlaybackEvents
    over broadcast and forwards them to the associated PlaybackEventListener.
 */

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PlaybackEventReceiver
{
    private final EventBus        eventBus;
    private PlaybackEventListener listener;

    public PlaybackEventReceiver(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }

    public PlaybackEventReceiver setListener(PlaybackEventListener listener)
    {
        this.listener = listener;
        return this;
    }

    public void subscribe()
    {
        eventBus.register(this);
    }

    public void unsubscribe()
    {
        eventBus.unregister(this);
    }

    @Subscribe
    public void onEvent(PlaybackEvent playbackEvent)
    {
        listener.onPlaybackEvent(playbackEvent);
    }
}
