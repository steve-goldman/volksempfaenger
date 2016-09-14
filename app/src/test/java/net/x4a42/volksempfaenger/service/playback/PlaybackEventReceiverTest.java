package net.x4a42.volksempfaenger.service.playback;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PlaybackEventReceiverTest
{
    @Mock EventBus                  eventBus;
    @Mock PlaybackEventListener     listener;
    PlaybackEventReceiver           receiver;

    @Before
    public void setUp() throws Exception
    {
        receiver = new PlaybackEventReceiver(eventBus).setListener(listener);
    }

    @Test
    public void testSubscribe() throws Exception
    {
        receiver.subscribe();

        Mockito.verify(eventBus).register(receiver);
    }

    @Test
    public void testUnsubscribe() throws Exception
    {
        receiver.unsubscribe();

        Mockito.verify(eventBus).unregister(receiver);
    }

    @Test
    public void onReceive()
    {
        receiver.onEvent(PlaybackEvent.PLAYING);

        Mockito.verify(listener).onPlaybackEvent(PlaybackEvent.PLAYING);
    }
}
