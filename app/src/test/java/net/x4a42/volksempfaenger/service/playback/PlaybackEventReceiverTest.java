package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PlaybackEventReceiverTest
{
    Context                   context                   = Mockito.mock(Context.class);
    PlaybackEventActionMapper playbackEventActionMapper = Mockito.mock(PlaybackEventActionMapper.class);
    PlaybackEventListener     listener                  = Mockito.mock(PlaybackEventListener.class);
    PlaybackEventReceiver     receiver;

    @Before
    public void setUp() throws Exception
    {
        receiver = new PlaybackEventReceiver(context, playbackEventActionMapper).setListener(listener);
    }

    @Test
    public void testSubscribe() throws Exception
    {
        receiver.subscribe();

        Mockito.verify(playbackEventActionMapper).getIntentFilter();
        Mockito.verify(context).registerReceiver(Mockito.eq(receiver), Mockito.any(IntentFilter.class));
    }

    @Test
    public void testUnsubscribe() throws Exception
    {
        receiver.unsubscribe();

        Mockito.verify(context).unregisterReceiver(receiver);
    }

    @Test
    public void onReceive()
    {
        Mockito.when(playbackEventActionMapper.isValid(Mockito.anyString())).thenReturn(true);

        receiver.onReceive(context, Mockito.mock(Intent.class));

        Mockito.verify(playbackEventActionMapper).isValid(Mockito.anyString());
        Mockito.verify(listener).onPlaybackEvent(Mockito.any(PlaybackEvent.class));
    }
}