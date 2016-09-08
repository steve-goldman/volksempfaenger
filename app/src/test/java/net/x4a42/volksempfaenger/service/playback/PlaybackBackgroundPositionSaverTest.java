package net.x4a42.volksempfaenger.service.playback;

import android.net.Uri;
import android.os.Handler;

import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PlaybackBackgroundPositionSaverTest
{
    EpisodeDataHelper               episodeDataHelper = Mockito.mock(EpisodeDataHelper.class);
    Handler                         handler           = Mockito.mock(Handler.class);
    Uri                             episodeUri        = Mockito.mock(Uri.class);
    PlaybackPositionProvider        positionProvider  = Mockito.mock(PlaybackPositionProvider.class);
    int                             position          = 10;
    PlaybackBackgroundPositionSaver backgroundSaver;

    @Before
    public void setUp() throws Exception
    {
        backgroundSaver = new PlaybackBackgroundPositionSaver(episodeDataHelper, handler);
        Mockito.when(positionProvider.getPosition()).thenReturn(position);
    }

    @Test
    public void start() throws Exception
    {
        backgroundSaver.start(episodeUri, positionProvider);

        Mockito.verify(handler).post(backgroundSaver);
    }

    @Test
    public void stop() throws Exception
    {
        backgroundSaver.start(episodeUri, positionProvider);
        backgroundSaver.stop();

        Mockito.verify(episodeDataHelper).setDurationListened(episodeUri, position);
        Mockito.verify(handler).removeCallbacks(backgroundSaver);
    }

    @Test
    public void run() throws Exception
    {
        backgroundSaver.start(episodeUri, positionProvider);

        backgroundSaver.run();

        Mockito.verify(episodeDataHelper).setDurationListened(episodeUri, position);
        Mockito.verify(handler).postDelayed(backgroundSaver, PlaybackBackgroundPositionSaver.Interval);
    }
}