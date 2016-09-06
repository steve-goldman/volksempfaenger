package net.x4a42.volksempfaenger.service.playback;

import android.os.Handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PlaybackBackgroundPositionSaverTest
{
    PlaybackPositionSaver           saver            = Mockito.mock(PlaybackPositionSaver.class);
    Handler                         handler          = Mockito.mock(Handler.class);
    PlaybackItem                    playbackItem     = Mockito.mock(PlaybackItem.class);
    PlaybackPositionProvider        positionProvider = Mockito.mock(PlaybackPositionProvider.class);
    int                             position         = 10;
    PlaybackBackgroundPositionSaver backgroundSaver;

    @Before
    public void setUp() throws Exception
    {
        backgroundSaver = new PlaybackBackgroundPositionSaver(saver, handler);
        Mockito.when(positionProvider.getPosition()).thenReturn(position);
    }

    @Test
    public void start() throws Exception
    {
        backgroundSaver.start(playbackItem, positionProvider);

        Mockito.verify(handler).post(backgroundSaver);
    }

    @Test
    public void stop() throws Exception
    {
        backgroundSaver.start(playbackItem, positionProvider);
        backgroundSaver.stop();

        Mockito.verify(saver).save(playbackItem, position);
        Mockito.verify(handler).removeCallbacks(backgroundSaver);
    }

    @Test
    public void run() throws Exception
    {
        backgroundSaver.start(playbackItem, positionProvider);

        backgroundSaver.run();

        Mockito.verify(saver).save(playbackItem, position);
        Mockito.verify(handler).postDelayed(backgroundSaver, PlaybackBackgroundPositionSaver.Interval);
    }
}