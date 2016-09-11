package net.x4a42.volksempfaenger.service.playback;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PlaybackEventBroadcasterTest
{
    EventBus                  eventBus = Mockito.mock(EventBus.class);
    PlaybackEventBroadcaster  playbackEventBroadcaster;

    @Before
    public void setUp() throws Exception
    {
        playbackEventBroadcaster = new PlaybackEventBroadcaster(eventBus);
    }

    @Test
    public void broadcast() throws Exception
    {
        playbackEventBroadcaster.broadcast(PlaybackEvent.PLAYING);

        Mockito.verify(eventBus).post(PlaybackEvent.PLAYING);
    }
}