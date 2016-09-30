package net.x4a42.volksempfaenger.service.playback;

import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IntentParserTest
{
    @Mock IntentParser.Listener listener;
    @Mock Intent                intent;
    int                         position  = 10;
    int                         offset    = -15;
    IntentParser                parser;

    @Before
    public void setUp() throws Exception
    {
        parser = new IntentParser().setListener(listener);
    }

    @Test
    public void parseNoListener() throws Exception
    {
        parser.setListener(null);

        parser.parse(intent);

        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void parseNoIntent() throws Exception
    {
        parser.parse(null);

        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void parseNoAction() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(null);

        parser.parse(intent);

        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void parsePlay() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(PlaybackService.ActionPlay);

        parser.parse(intent);

        Mockito.verify(listener).onPlay();
    }

    @Test
    public void parsePause() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(PlaybackService.ActionPause);

        parser.parse(intent);

        Mockito.verify(listener).onPause();
    }

    @Test
    public void parsePlayPause() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(PlaybackService.ActionPlayPause);

        parser.parse(intent);

        Mockito.verify(listener).onPlayPause();
    }

    @Test
    public void parseStop() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(PlaybackService.ActionStop);

        parser.parse(intent);

        Mockito.verify(listener).onStop();
    }

    @Test
    public void parseSeek() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(PlaybackService.ActionSeek);
        Mockito.when(intent.getIntExtra(PlaybackServiceIntentProvider.PositionKey, 0)).thenReturn(position);

        parser.parse(intent);

        Mockito.verify(listener).onSeek(position);
    }

    @Test
    public void parseMove() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(PlaybackService.ActionMove);
        Mockito.when(intent.getIntExtra(PlaybackServiceIntentProvider.OffsetKey, 0)).thenReturn(offset);

        parser.parse(intent);

        Mockito.verify(listener).onMove(offset);
    }

}
