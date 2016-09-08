package net.x4a42.volksempfaenger.service.playback;

import android.media.MediaPlayer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

public class PlaybackControllerTest
{
    PlaybackEventBroadcaster  playbackEventBroadcaster  = Mockito.mock(PlaybackEventBroadcaster.class);
    MediaPlayer               mediaPlayer               = Mockito.mock(MediaPlayer.class);
    AudioFocusManager         audioFocusManager         = Mockito.mock(AudioFocusManager.class);
    AudioBecomingNoisyManager audioBecomingNoisyManager = Mockito.mock(AudioBecomingNoisyManager.class);
    PlaybackItem              playbackItem              = Mockito.mock(PlaybackItem.class);
    PlaybackEventListener     playbackEventListener     = Mockito.mock(PlaybackEventListener.class);
    String                    file                      = "this-is-my-file";
    int                       seekToPosition            = 123;
    PlaybackController        playbackController;

    @Before
    public void setUp() throws Exception
    {
        playbackController = Mockito.spy(new PlaybackController(playbackEventBroadcaster,
                                                                mediaPlayer,
                                                                audioFocusManager,
                                                                audioBecomingNoisyManager)
                                                 .setListener(playbackEventListener));
        Mockito.when(playbackItem.getPath()).thenReturn(file);
    }

    //
    // public methods
    //

    @Test
    public void open() throws Exception
    {
        InOrder inOrder = Mockito.inOrder(playbackEventListener, playbackEventBroadcaster, mediaPlayer);

        playbackController.open(playbackItem);

        inOrder.verify(mediaPlayer).setDataSource(file);
        inOrder.verify(mediaPlayer).prepareAsync();
    }

    @Test
    public void play() throws Exception
    {
        playbackController.play();

        InOrder inOrder = Mockito.inOrder(audioFocusManager, mediaPlayer, playbackEventListener, playbackEventBroadcaster);
        inOrder.verify(audioFocusManager).requestFocus();
        inOrder.verify(mediaPlayer).start();
        inOrder.verify(playbackEventListener).onPlaybackEvent(PlaybackEvent.PLAYING);
        inOrder.verify(playbackEventBroadcaster).broadcast(PlaybackEvent.PLAYING);
    }

    @Test
    public void pause() throws Exception
    {
        playbackController.pause();

        InOrder inOrder = Mockito.inOrder(mediaPlayer, playbackEventListener, playbackEventBroadcaster);
        inOrder.verify(mediaPlayer).pause();
        inOrder.verify(playbackEventListener).onPlaybackEvent(PlaybackEvent.PAUSED);
        inOrder.verify(playbackEventBroadcaster).broadcast(PlaybackEvent.PAUSED);
    }

    @Test
    public void stopPreparedPrepared() throws Exception
    {
        playbackController.open(playbackItem);
        playbackController.onPrepared(mediaPlayer);
        playbackController.stop();

        InOrder inOrder = Mockito.inOrder(audioFocusManager, mediaPlayer, playbackEventListener, playbackEventBroadcaster);
        inOrder.verify(mediaPlayer).stop();
        inOrder.verify(audioFocusManager).abandonFocus();
        inOrder.verify(mediaPlayer).reset();
    }

    @Test
    public void stopNotPrepared() throws Exception
    {
        playbackController.stop();

        InOrder inOrder = Mockito.inOrder(audioFocusManager, mediaPlayer, playbackEventListener, playbackEventBroadcaster);
        inOrder.verify(audioFocusManager).abandonFocus();
        inOrder.verify(mediaPlayer).reset();
    }

    @Test
    public void seekTo() throws Exception
    {
        playbackController.seekTo(seekToPosition);

        Mockito.verify(mediaPlayer).seekTo(seekToPosition);
    }

    @Test
    public void movePosition()
    {
        Mockito.when(mediaPlayer.getCurrentPosition()).thenReturn(10);
        Mockito.when(mediaPlayer.getDuration()).thenReturn(20);

        playbackController.movePosition(5);

        Mockito.verify(mediaPlayer).seekTo(15);
    }

    @Test
    public void movePositionUnderflow()
    {
        Mockito.when(mediaPlayer.getCurrentPosition()).thenReturn(10);
        Mockito.when(mediaPlayer.getDuration()).thenReturn(20);

        playbackController.movePosition(-15);

        Mockito.verify(mediaPlayer).seekTo(0);
    }

    @Test
    public void movePositionOverflow()
    {
        Mockito.when(mediaPlayer.getCurrentPosition()).thenReturn(10);
        Mockito.when(mediaPlayer.getDuration()).thenReturn(20);

        playbackController.movePosition(15);

        Mockito.verify(mediaPlayer).seekTo(20);
    }

    @Test
    public void destroy()
    {
        playbackController.destroy();

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
        int duration = 10;
        Mockito.when(playbackItem.getDurationListenedAtStart()).thenReturn(duration);

        playbackController.open(playbackItem);
        playbackController.onPrepared(mediaPlayer);

        Mockito.verify(playbackItem).getDurationListenedAtStart();
        Mockito.verify(playbackController).play();
    }

    //
    // MediaPlayer.OnCompletionListener
    //

    @Test
    public void onCompletion() throws Exception
    {
        InOrder inOrder = Mockito.inOrder(playbackEventListener, playbackEventBroadcaster, mediaPlayer);

        playbackController.onCompletion(mediaPlayer);

        inOrder.verify(playbackEventListener).onPlaybackEvent(PlaybackEvent.ENDED);
        inOrder.verify(playbackEventBroadcaster).broadcast(PlaybackEvent.ENDED);
        inOrder.verify(mediaPlayer).reset();
    }

    //
    // AudioFocusManager.Listener
    //

    @Test
    public void onAudioFocusGained() throws Exception
    {
        playbackController.onAudioFocusGained();

        Mockito.verify(mediaPlayer).setVolume(PlaybackController.FullVolume, PlaybackController.FullVolume);
        Mockito.verify(playbackController, Mockito.never()).play();
    }

    @Test
    public void onAudioFocusGainedWasTransientlyPaused() throws Exception
    {
        Mockito.when(mediaPlayer.isPlaying()).thenReturn(true);

        playbackController.onAudioFocusLostTransiently();
        playbackController.onAudioFocusGained();

        InOrder inOrder = Mockito.inOrder(mediaPlayer, playbackController);
        inOrder.verify(playbackController).pause();
        inOrder.verify(playbackController).play();
    }

    @Test
    public void onAudioFocusLost() throws Exception
    {
        Mockito.when(mediaPlayer.isPlaying()).thenReturn(true);

        playbackController.onAudioFocusLost();

        Mockito.verify(playbackController).pause();
    }

    @Test
    public void onAudioFocusLostNotPlaying() throws Exception
    {
        playbackController.onAudioFocusLost();

        Mockito.verify(playbackController, Mockito.never()).pause();
    }

    @Test
    public void onAudioFocusTransientlyLost() throws Exception
    {
        Mockito.when(mediaPlayer.isPlaying()).thenReturn(true);

        playbackController.onAudioFocusLostTransiently();

        Mockito.verify(playbackController).pause();
    }

    @Test
    public void onAudioFocusTransientlyLostNotPlaying() throws Exception
    {
        playbackController.onAudioFocusLostTransiently();

        Mockito.verify(playbackController, Mockito.never()).pause();
    }

    @Test
    public void onAudioFocusTransientlyLostCanDuck() throws Exception
    {
        Mockito.when(mediaPlayer.isPlaying()).thenReturn(true);

        playbackController.onAudioFocusLostTransientlyCanDuck();

        Mockito.verify(mediaPlayer).setVolume(PlaybackController.DuckedVolume, PlaybackController.DuckedVolume);
    }

    @Test
    public void onAudioFocusTransientlyLostCanDuckNotPlaying() throws Exception
    {
        playbackController.onAudioFocusLostTransientlyCanDuck();

        Mockito.verify(mediaPlayer, Mockito.never()).setVolume(PlaybackController.DuckedVolume,
                                                               PlaybackController.DuckedVolume);
    }

    //
    // AudioBecomingNoisyManager.Listener
    //

    @Test
    public void onAudioBecomingNoisy()
    {
        Mockito.when(mediaPlayer.isPlaying()).thenReturn(true);

        playbackController.onAudioBecomingNoisy();

        Mockito.verify(playbackController).pause();
    }

    @Test
    public void onAudioBecomingNoisyNotPlaying()
    {
        playbackController.onAudioBecomingNoisy();

        Mockito.verify(playbackController, Mockito.never()).pause();
    }
}