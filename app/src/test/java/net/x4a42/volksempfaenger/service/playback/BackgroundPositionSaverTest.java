package net.x4a42.volksempfaenger.service.playback;

import android.os.Handler;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
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
    @Mock Episode episode;
    @Mock PlaybackPositionProvider        positionProvider;
    int                                   position          = 10;
    BackgroundPositionSaver               backgroundSaver;

    @Before
    public void setUp() throws Exception
    {
        backgroundSaver = new BackgroundPositionSaver(handler, positionProvider);
        Mockito.when(positionProvider.getPosition()).thenReturn(position);
    }

    @Test
    public void start() throws Exception
    {
        backgroundSaver.start(episode);

        Mockito.verify(handler).post(backgroundSaver);
    }

    @Test
    public void stopReset() throws Exception
    {
        backgroundSaver.start(episode);
        backgroundSaver.stop(true);

        Mockito.verify(handler).removeCallbacks(backgroundSaver);
    }

    @Test
    public void stopNoReset() throws Exception
    {
        backgroundSaver.start(episode);
        backgroundSaver.stop(false);

        Mockito.verify(handler).removeCallbacks(backgroundSaver);
    }

    @Test
    public void run() throws Exception
    {
        backgroundSaver.start(episode);

        backgroundSaver.run();

        Mockito.verify(handler).postDelayed(backgroundSaver, BackgroundPositionSaver.Interval);
    }
}
