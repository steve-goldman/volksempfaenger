package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PlaybackEventBroadcasterTest
{
    Context                   context                   = Mockito.mock(Context.class);
    PlaybackEventActionMapper playbackEventActionMapper = Mockito.mock(PlaybackEventActionMapper.class);
    PlaybackEventBroadcaster  playbackEventBroadcaster;

    @Before
    public void setUp() throws Exception
    {
        playbackEventBroadcaster = new PlaybackEventBroadcaster(context, playbackEventActionMapper);
    }

    @Test
    public void broadcast() throws Exception
    {
        playbackEventBroadcaster.broadcast(PlaybackEvent.PLAYING);

        Mockito.verify(playbackEventActionMapper).getAction(PlaybackEvent.PLAYING);
        Mockito.verify(context).sendBroadcast(Mockito.any(Intent.class));
    }
}