package net.x4a42.volksempfaenger.service.playback;

import android.net.Uri;
import android.os.Handler;

import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BackgroundPositionSaverTest
{
    @Mock EpisodeDataHelper               episodeDataHelper;
    @Mock Handler                         handler;
    @Mock Uri                             episodeUri;
    @Mock PlaybackPositionProvider        positionProvider;
    int                                   position          = 10;
    BackgroundPositionSaver               backgroundSaver;

    @Before
    public void setUp() throws Exception
    {
        backgroundSaver = new BackgroundPositionSaver(episodeDataHelper, handler);
        Mockito.when(positionProvider.getPosition()).thenReturn(position);
    }

    @Test
    public void start() throws Exception
    {
        backgroundSaver.start(episodeUri, positionProvider);

        Mockito.verify(episodeDataHelper).markListening(episodeUri);
        Mockito.verify(handler).post(backgroundSaver);
    }

    @Test
    public void stopReset() throws Exception
    {
        backgroundSaver.start(episodeUri, positionProvider);
        backgroundSaver.stop(true);

        Mockito.verify(episodeDataHelper).setDurationListened(episodeUri, 0);
        Mockito.verify(handler).removeCallbacks(backgroundSaver);
    }

    @Test
    public void stopNoReset() throws Exception
    {
        backgroundSaver.start(episodeUri, positionProvider);
        backgroundSaver.stop(false);

        Mockito.verify(episodeDataHelper).setDurationListened(episodeUri, position);
        Mockito.verify(handler).removeCallbacks(backgroundSaver);
    }

    @Test
    public void run() throws Exception
    {
        backgroundSaver.start(episodeUri, positionProvider);

        backgroundSaver.run();

        Mockito.verify(episodeDataHelper).setDurationListened(episodeUri, position);
        Mockito.verify(handler).postDelayed(backgroundSaver, BackgroundPositionSaver.Interval);
    }
}
