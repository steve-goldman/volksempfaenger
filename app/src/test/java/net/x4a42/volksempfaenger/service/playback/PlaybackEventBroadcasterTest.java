package net.x4a42.volksempfaenger.service.playback;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PlaybackEventBroadcasterTest
{
    @Mock EventBus                  eventBus;
    PlaybackEventBroadcaster        playbackEventBroadcaster;

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
