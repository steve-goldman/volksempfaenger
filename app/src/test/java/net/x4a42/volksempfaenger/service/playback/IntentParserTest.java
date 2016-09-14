package net.x4a42.volksempfaenger.service.playback;

import android.content.Intent;
import android.net.Uri;

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
    @Mock Uri                   episodeUri;
    int                         position = 10;
    int                         offset   = -15;
    IntentParser                parser;

    @Before
    public void setUp() throws Exception
    {
        parser = new IntentParser().setListener(listener);
        Mockito.when(intent.getData()).thenReturn(episodeUri);
    }

    @Test
    public void testParseNoListener() throws Exception
    {
        parser.setListener(null);

        parser.parse(intent);

        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testParseNoIntent() throws Exception
    {
        parser.parse(null);

        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testParseNoAction() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(null);

        parser.parse(intent);

        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testParsePlay() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(PlaybackService.ActionPlay);

        parser.parse(intent);

        Mockito.verify(listener).onPlay(episodeUri);
    }

    @Test
    public void testParsePause() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(PlaybackService.ActionPause);

        parser.parse(intent);

        Mockito.verify(listener).onPause();
    }

    @Test
    public void testParsePlayPause() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(PlaybackService.ActionPlayPause);

        parser.parse(intent);

        Mockito.verify(listener).onPlayPause();
    }

    @Test
    public void testParseStop() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(PlaybackService.ActionStop);

        parser.parse(intent);

        Mockito.verify(listener).onStop();
    }

    @Test
    public void testParseSeek() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(PlaybackService.ActionSeek);
        Mockito.when(intent.getIntExtra(PlaybackServiceIntentProvider.PositionKey, 0)).thenReturn(position);

        parser.parse(intent);

        Mockito.verify(listener).onSeek(position);
    }

    @Test
    public void testParseMove() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(PlaybackService.ActionMove);
        Mockito.when(intent.getIntExtra(PlaybackServiceIntentProvider.OffsetKey, 0)).thenReturn(offset);

        parser.parse(intent);

        Mockito.verify(listener).onMove(offset);
    }

}
