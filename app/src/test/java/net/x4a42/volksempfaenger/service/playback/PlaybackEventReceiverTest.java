package net.x4a42.volksempfaenger.service.playback;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PlaybackEventReceiverTest
{
    EventBus                  eventBus = Mockito.mock(EventBus.class);
    PlaybackEventListener     listener = Mockito.mock(PlaybackEventListener.class);
    PlaybackEventReceiver     receiver;

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