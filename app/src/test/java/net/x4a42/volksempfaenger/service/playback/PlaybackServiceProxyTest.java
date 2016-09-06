package net.x4a42.volksempfaenger.service.playback;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;

import net.x4a42.volksempfaenger.misc.EpisodeDataHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class PlaybackServiceProxyTest
{
    PlaybackService                    playbackService            = Mockito.mock(PlaybackService.class);
    PlaybackBackgroundPositionSaver    positionSaver              = Mockito.mock(PlaybackBackgroundPositionSaver.class);
    PlaybackController                 controller                 = Mockito.mock(PlaybackController.class);
    PlaybackItemBuilder                playbackItemBuilder        = Mockito.mock(PlaybackItemBuilder.class);
    PlaybackServiceIntentParser        intentParser               = Mockito.mock(PlaybackServiceIntentParser.class);
    EpisodeDataHelper                  episodeDataHelper          = Mockito.mock(EpisodeDataHelper.class);
    PlaybackMediaButtonReceiver        mediaButtonReceiver        = Mockito.mock(PlaybackMediaButtonReceiver.class);
    MediaSessionManager                mediaSessionManager        = Mockito.mock(MediaSessionManager.class);
    PlaybackNotificationManagerBuilder notificationManagerBuilder = Mockito.mock(PlaybackNotificationManagerBuilder.class);
    PlaybackNotificationManager        notificationManager        = Mockito.mock(PlaybackNotificationManager.class);
    PlaybackItem                       playbackItem               = Mockito.mock(PlaybackItem.class);
    PlaybackItem                       otherPlaybackItem          = Mockito.mock(PlaybackItem.class);
    Intent                             intent                     = Mockito.mock(Intent.class);
    Uri                                episodeUri                 = Mockito.mock(Uri.class);
    Uri                                otherEpisodeUri            = Mockito.mock(Uri.class);
    PlaybackServiceProxy               proxy;

    @Before
    public void setUp() throws Exception
    {
        proxy = Mockito.spy(
                new PlaybackServiceProxy(playbackService,
                                         positionSaver,
                                         controller,
                                         playbackItemBuilder,
                                         intentParser,
                                         episodeDataHelper,
                                         mediaButtonReceiver,
                                         mediaSessionManager,
                                         notificationManagerBuilder));

        Mockito.when(playbackItemBuilder.build(playbackService, episodeUri)).thenReturn(playbackItem);
        Mockito.when(playbackItem.getEpisodeUri()).thenReturn(episodeUri);
        Mockito.when(otherPlaybackItem.getEpisodeUri()).thenReturn(otherEpisodeUri);
        Mockito.when(notificationManagerBuilder.build(playbackService, playbackItem)).thenReturn(
                notificationManager);
    }

    @Test
    public void onStartCommand() throws Exception
    {
        int result = proxy.onStartCommand(intent);
        Mockito.verify(intentParser).parse(intent);
        assertEquals(Service.START_STICKY, result);
    }

    @Test
    public void onPlayNoEpisodeNoneOpen() throws Exception
    {
        proxy.onPlay(null);

        Mockito.verify(playbackItemBuilder, Mockito.never()).build(playbackService, episodeUri);
        Mockito.verify(controller, Mockito.never()).open(playbackItem);

        // TODO: check episode status is EPISODE_STATE_LISTENING
    }

    @Test
    public void onPlayPlayingNoneOrOther() throws Exception
    {
        proxy.onPlay(episodeUri);

        Mockito.verify(playbackItemBuilder).build(playbackService, episodeUri);
        Mockito.verify(controller).open(playbackItem);

        // TODO: check episode status is EPISODE_STATE_LISTENING
    }

    @Test
    public void onPlayPlayingSamePlaying() throws Exception
    {
        Mockito.when(controller.isPlaybackItemOpen(playbackItem)).thenReturn(true);
        Mockito.when(controller.isPlaying()).thenReturn(true);

        proxy.onPlay(episodeUri);

        Mockito.verify(playbackItemBuilder).build(playbackService, episodeUri);
        Mockito.verify(controller, Mockito.never()).play();

        // TODO: check episode status is EPISODE_STATE_LISTENING
    }

    @Test
    public void onPlayPlayingSamePaused() throws Exception
    {
        Mockito.when(controller.isPlaybackItemOpen(playbackItem)).thenReturn(true);
        Mockito.when(controller.isPlaying()).thenReturn(false);

        proxy.onPlay(episodeUri);

        Mockito.verify(playbackItemBuilder).build(playbackService, episodeUri);
        Mockito.verify(controller).play();

        // TODO: check episode status is EPISODE_STATE_LISTENING
    }

    @Test
    public void onPausePlaying() throws Exception
    {
        Mockito.when(controller.isPlaying()).thenReturn(true);

        proxy.onPause();

        Mockito.verify(controller).pause();
    }

    @Test
    public void onPausePaused() throws Exception
    {
        Mockito.when(controller.isPlaying()).thenReturn(false);

        proxy.onPause();

        Mockito.verify(controller, Mockito.never()).pause();
    }

    @Test
    public void onPlayPauseNotPlaying()
    {
        proxy.onPlayPause();

        Mockito.verify(controller).isOpen();
        Mockito.verifyNoMoreInteractions(controller);
    }

    @Test
    public void onPlayPausePaused()
    {
        Mockito.when(controller.isOpen()).thenReturn(true);

        proxy.onPlayPause();

        Mockito.verify(controller).play();
    }

    @Test
    public void onPlayPausedPlaying()
    {
        Mockito.when(controller.isOpen()).thenReturn(true);
        Mockito.when(controller.isPlaying()).thenReturn(true);

        proxy.onPlayPause();

        Mockito.verify(controller).pause();
    }

    @Test
    public void onStop() throws Exception
    {
        Mockito.when(controller.getPlaybackItem()).thenReturn(playbackItem);

        proxy.onPlaybackEvent(PlaybackEvent.PLAYING);
        proxy.onStop();

        Mockito.verify(proxy).onPause();
        Mockito.verify(controller).stop();
        Mockito.verify(notificationManager).remove();
    }

    @Test
    public void onSeekNotPlaying() throws Exception
    {
        int position = 10;

        proxy.onSeek(position);

        Mockito.verify(controller, Mockito.never()).seekTo(Mockito.anyInt());
    }

    @Test
    public void onSeekPlaying() throws Exception
    {
        int position = 10;
        Mockito.when(controller.isPlaying()).thenReturn(true);

        proxy.onSeek(position);

        Mockito.verify(controller).seekTo(position);
    }

    @Test
    public void onMoveNotPlaying() throws Exception
    {
        int offset = 15;

        proxy.onMove(offset);

        Mockito.verify(controller, Mockito.never()).movePosition(Mockito.anyInt());
    }

    @Test
    public void onMovePlaying() throws Exception
    {
        int offset = 15;
        Mockito.when(controller.isPlaying()).thenReturn(true);

        proxy.onMove(offset);

        Mockito.verify(controller).movePosition(offset);
    }

    @Test
    public void onDestroy() throws Exception
    {
        proxy.onDestroy();

        Mockito.verify(controller).destroy();
        Mockito.verify(mediaButtonReceiver).destroy();
        Mockito.verify(mediaSessionManager).destroy();
    }

    @Test
    public void onPlaybackEventPlaying() throws Exception
    {
        Mockito.when(controller.getPlaybackItem()).thenReturn(playbackItem);

        proxy.onPlaybackEvent(PlaybackEvent.PLAYING);

        Mockito.verify(notificationManagerBuilder).build(playbackService, playbackItem);
        Mockito.verify(notificationManager).updateForPlay();
        Mockito.verify(positionSaver).start(playbackItem, controller);
        Mockito.verify(episodeDataHelper).markAsListened(episodeUri);
    }

    @Test
    public void onPlaybackEventPaused() throws Exception
    {
        Mockito.when(controller.getPlaybackItem()).thenReturn(playbackItem);

        proxy.onPlaybackEvent(PlaybackEvent.PLAYING);
        proxy.onPlaybackEvent(PlaybackEvent.PAUSED);

        Mockito.verify(notificationManager).updateForPause(playbackItem);
        Mockito.verify(positionSaver).stop();
    }

    @Test
    public void onPlaybackEventEnded() throws Exception
    {
        Mockito.when(controller.getPlaybackItem()).thenReturn(playbackItem);

        proxy.onPlaybackEvent(PlaybackEvent.PLAYING);
        proxy.onPlaybackEvent(PlaybackEvent.ENDED);

        Mockito.verify(notificationManager).remove();
        Mockito.verify(positionSaver).stop();
    }
}