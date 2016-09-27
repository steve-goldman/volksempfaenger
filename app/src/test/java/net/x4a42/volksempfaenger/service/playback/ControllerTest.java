package net.x4a42.volksempfaenger.service.playback;

import android.media.MediaPlayer;

import net.x4a42.volksempfaenger.data.entity.enclosure.Enclosure;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePosition;
import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDaoWrapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest
{
    @Mock PlaybackEventBroadcaster  playbackEventBroadcaster;
    @Mock MediaPlayer               mediaPlayer;
    @Mock AudioFocusManager         audioFocusManager;
    @Mock AudioBecomingNoisyManager audioBecomingNoisyManager;
    @Mock Episode                   playbackEpisode;
    @Mock EpisodePosition           episodePosition;
    @Mock List<Enclosure>           list;
    @Mock Enclosure                 enclosure;
    @Mock PlaybackEventListener     playbackEventListener;
    @Mock EpisodePositionDaoWrapper episodePositionDao;
    String                          url                       = "this-is-my-url";
    int                             seekToPosition            = 123;
    Controller                      controller;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(playbackEpisode.getEnclosures()).thenReturn(list);
        Mockito.when(list.get(0)).thenReturn(enclosure);
        Mockito.when(enclosure.getUrl()).thenReturn(url);
        Mockito.when(episodePositionDao.getOrCreate(playbackEpisode)).thenReturn(episodePosition);
        Mockito.when(episodePosition.getPosition()).thenReturn(seekToPosition);
        controller = Mockito.spy(new Controller(playbackEventBroadcaster,
                                                mediaPlayer,
                                                audioFocusManager,
                                                audioBecomingNoisyManager,
                                                episodePositionDao)
                                         .setListener(playbackEventListener));
    }

    //
    // public methods
    //

    @Test
    public void open() throws Exception
    {
        InOrder inOrder = Mockito.inOrder(playbackEventListener, playbackEventBroadcaster, mediaPlayer);

        controller.open(playbackEpisode);

        inOrder.verify(mediaPlayer).setDataSource(url);
        inOrder.verify(mediaPlayer).prepareAsync();
    }

    @Test
    public void play() throws Exception
    {
        controller.play();

        InOrder inOrder = Mockito.inOrder(audioFocusManager, mediaPlayer, playbackEventListener, playbackEventBroadcaster);
        inOrder.verify(audioFocusManager).requestFocus();
        inOrder.verify(mediaPlayer).start();
        inOrder.verify(playbackEventListener).onPlaybackEvent(PlaybackEvent.PLAYING);
        inOrder.verify(playbackEventBroadcaster).broadcast(PlaybackEvent.PLAYING);
    }

    @Test
    public void pause() throws Exception
    {
        controller.pause();

        InOrder inOrder = Mockito.inOrder(mediaPlayer, playbackEventListener, playbackEventBroadcaster);
        inOrder.verify(mediaPlayer).pause();
        inOrder.verify(playbackEventListener).onPlaybackEvent(PlaybackEvent.PAUSED);
        inOrder.verify(playbackEventBroadcaster).broadcast(PlaybackEvent.PAUSED);
    }

    @Test
    public void stopPreparedPrepared() throws Exception
    {
        controller.open(playbackEpisode);
        controller.onPrepared(mediaPlayer);
        controller.stop();

        InOrder inOrder = Mockito.inOrder(audioFocusManager, mediaPlayer, playbackEventListener, playbackEventBroadcaster);
        inOrder.verify(mediaPlayer).stop();
        inOrder.verify(audioFocusManager).abandonFocus();
        inOrder.verify(mediaPlayer).reset();
    }

    @Test
    public void stopNotPrepared() throws Exception
    {
        controller.stop();

        InOrder inOrder = Mockito.inOrder(audioFocusManager, mediaPlayer, playbackEventListener, playbackEventBroadcaster);
        inOrder.verify(audioFocusManager).abandonFocus();
        inOrder.verify(mediaPlayer).reset();
    }

    @Test
    public void seekTo() throws Exception
    {
        controller.seekTo(seekToPosition);

        Mockito.verify(mediaPlayer).seekTo(seekToPosition);
    }

    @Test
    public void movePosition()
    {
        Mockito.when(mediaPlayer.getCurrentPosition()).thenReturn(10);
        Mockito.when(mediaPlayer.getDuration()).thenReturn(20);

        controller.open(playbackEpisode);
        controller.onPrepared(mediaPlayer);
        controller.movePosition(5);

        Mockito.verify(mediaPlayer).seekTo(15);
    }

    @Test
    public void movePositionUnderflow()
    {
        Mockito.when(mediaPlayer.getCurrentPosition()).thenReturn(10);
        Mockito.when(mediaPlayer.getDuration()).thenReturn(20);

        controller.movePosition(-15);

        Mockito.verify(mediaPlayer).seekTo(0);
    }

    @Test
    public void movePositionOverflow()
    {
        Mockito.when(mediaPlayer.getCurrentPosition()).thenReturn(10);
        Mockito.when(mediaPlayer.getDuration()).thenReturn(20);

        controller.open(playbackEpisode);
        controller.onPrepared(mediaPlayer);
        controller.movePosition(15);

        Mockito.verify(mediaPlayer).seekTo(20);
    }

    @Test
    public void destroy()
    {
        controller.destroy();

        InOrder inOrder = Mockito.inOrder(playbackEventListener, playbackEventBroadcaster, audioBecomingNoisyManager, mediaPlayer);
        inOrder.verify(audioBecomingNoisyManager).stop();
        inOrder.verify(mediaPlayer).release();
    }

    //
    // MediaPlayer.OnPreparedListener
    //

    @Test
    public void onPrepared() throws Exception
    {
        controller.open(playbackEpisode);
        controller.onPrepared(mediaPlayer);

        Mockito.verify(controller).seekTo(seekToPosition);
        Mockito.verify(controller).play();
    }

    //
    // MediaPlayer.OnCompletionListener
    //

    @Test
    public void onCompletion() throws Exception
    {
        InOrder inOrder = Mockito.inOrder(playbackEventListener, playbackEventBroadcaster, mediaPlayer);

        controller.onCompletion(mediaPlayer);

        inOrder.verify(mediaPlayer).reset();
        inOrder.verify(playbackEventListener).onPlaybackEvent(PlaybackEvent.ENDED);
        inOrder.verify(playbackEventBroadcaster).broadcast(PlaybackEvent.ENDED);
    }

    //
    // AudioFocusManager.Listener
    //

    @Test
    public void onAudioFocusGained() throws Exception
    {
        controller.onAudioFocusGained();

        Mockito.verify(mediaPlayer).setVolume(Controller.FullVolume, Controller.FullVolume);
        Mockito.verify(controller, Mockito.never()).play();
    }

    @Test
    public void onAudioFocusGainedWasTransientlyPaused() throws Exception
    {
        Mockito.when(mediaPlayer.isPlaying()).thenReturn(true);

        controller.onAudioFocusLostTransiently();
        controller.onAudioFocusGained();

        InOrder inOrder = Mockito.inOrder(mediaPlayer, controller);
        inOrder.verify(controller).pause();
        inOrder.verify(controller).play();
    }

    @Test
    public void onAudioFocusLost() throws Exception
    {
        Mockito.when(mediaPlayer.isPlaying()).thenReturn(true);

        controller.onAudioFocusLost();

        Mockito.verify(controller).pause();
    }

    @Test
    public void onAudioFocusLostNotPlaying() throws Exception
    {
        controller.onAudioFocusLost();

        Mockito.verify(controller, Mockito.never()).pause();
    }

    @Test
    public void onAudioFocusTransientlyLost() throws Exception
    {
        Mockito.when(mediaPlayer.isPlaying()).thenReturn(true);

        controller.onAudioFocusLostTransiently();

        Mockito.verify(controller).pause();
    }

    @Test
    public void onAudioFocusTransientlyLostNotPlaying() throws Exception
    {
        controller.onAudioFocusLostTransiently();

        Mockito.verify(controller, Mockito.never()).pause();
    }

    @Test
    public void onAudioFocusTransientlyLostCanDuck() throws Exception
    {
        Mockito.when(mediaPlayer.isPlaying()).thenReturn(true);

        controller.onAudioFocusLostTransientlyCanDuck();

        Mockito.verify(mediaPlayer).setVolume(Controller.DuckedVolume, Controller.DuckedVolume);
    }

    @Test
    public void onAudioFocusTransientlyLostCanDuckNotPlaying() throws Exception
    {
        controller.onAudioFocusLostTransientlyCanDuck();

        Mockito.verify(mediaPlayer, Mockito.never()).setVolume(Controller.DuckedVolume,
                                                               Controller.DuckedVolume);
    }

    //
    // AudioBecomingNoisyManager.Listener
    //

    @Test
    public void onAudioBecomingNoisy()
    {
        Mockito.when(mediaPlayer.isPlaying()).thenReturn(true);

        controller.onAudioBecomingNoisy();

        Mockito.verify(controller).pause();
    }

    @Test
    public void onAudioBecomingNoisyNotPlaying()
    {
        controller.onAudioBecomingNoisy();

        Mockito.verify(controller, Mockito.never()).pause();
    }
}
