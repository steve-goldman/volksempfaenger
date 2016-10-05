package net.x4a42.volksempfaenger.service.playback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.playlist.Playlist;
import net.x4a42.volksempfaenger.service.playlistdownload.EpisodeDownloadEventReceiver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class PlaybackServiceProxyTest
{
    @Mock PlaybackService                    playbackService;
    @Mock BackgroundPositionSaver            positionSaver;
    @Mock Controller                         controller;
    @Mock IntentParser                       intentParser;
    @Mock MediaButtonReceiver                mediaButtonReceiver;
    @Mock MediaSessionManager                mediaSessionManager;
    @Mock NotificationManager                notificationManager;
    @Mock PlaybackNotificationBuilder        notificationBuilder;
    @Mock Notification                       notificationPlaying;
    @Mock Notification                       notificationPaused;
    @Mock Intent                             intent;
    @Mock Episode                            episode;
    @Mock Uri                                otherEpisodeUri;
    @Mock Playlist                           playlist;
    @Mock EpisodeDownloadEventReceiver       episodeDownloadEventReceiver;
    PlaybackServiceProxy                     proxy;

    @Before
    public void setUp() throws Exception
    {
        proxy = Mockito.spy(
                new PlaybackServiceProxy(positionSaver,
                                         controller,
                                         intentParser,
                                         mediaButtonReceiver,
                                         mediaSessionManager,
                                         notificationManager,
                                         notificationBuilder,
                                         playlist,
                                         episodeDownloadEventReceiver));

        Mockito.when(notificationBuilder.build(episode, true)).thenReturn(notificationPlaying);
        Mockito.when(notificationBuilder.build(episode, false)).thenReturn(notificationPaused);
        Mockito.when(playlist.getCurrentEpisode()).thenReturn(episode);
    }

    @Test
    public void onStartCommand() throws Exception
    {
        int result = proxy.onStartCommand(intent);
        Mockito.verify(intentParser).parse(intent);
        assertEquals(Service.START_STICKY, result);
    }

    @Test
    public void onPlayEmptyPlaylist() throws Exception
    {
        Mockito.when(playlist.isEmpty()).thenReturn(true);

        proxy.onPlay();

        Mockito.verify(controller, Mockito.never()).play();
        Mockito.verify(controller, Mockito.never()).open(Mockito.any(Episode.class));
    }

    @Test
    public void onPlayNotOpen() throws Exception
    {
        proxy.onPlay();

        Mockito.verify(controller, Mockito.never()).play();
        Mockito.verify(controller).open(episode);
    }

    @Test
    public void onPlayOpenNotPlaying() throws Exception
    {
        Mockito.when(controller.isPlaybackEpisodeOpen(episode)).thenReturn(true);

        proxy.onPlay();

        Mockito.verify(controller).play();
        Mockito.verify(controller, Mockito.never()).open(Mockito.any(Episode.class));
    }

    @Test
    public void onPlayOpenPlaying() throws Exception
    {
        Mockito.when(controller.isPlaybackEpisodeOpen(episode)).thenReturn(true);
        Mockito.when(controller.isPlaying()).thenReturn(true);

        proxy.onPlay();

        Mockito.verify(controller, Mockito.never()).play();
        Mockito.verify(controller, Mockito.never()).open(Mockito.any(Episode.class));
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

        Mockito.verify(proxy).onPlay();
    }

    @Test
    public void onPlayPausePaused()
    {
        Mockito.when(controller.isOpen()).thenReturn(true);

        proxy.onPlayPause();

        Mockito.verify(proxy).onPlay();
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
        Mockito.when(controller.getPlaybackEpisode()).thenReturn(episode);

        proxy.onPlaybackEvent(PlaybackEvent.PLAYING);
        proxy.onStop();

        Mockito.verify(proxy).onPause();
        Mockito.verify(controller).stop();
        Mockito.verify(notificationManager).cancel(PlaybackServiceProxy.NotificationId);
    }

    @Test
    public void onSeekNotPlaying() throws Exception
    {
        int position = 10;

        proxy.onSeek(position);

        Mockito.verify(controller, Mockito.never()).seekTo(Mockito.anyInt());
    }

    @Test
    public void onSeekOpen() throws Exception
    {
        int position = 10;
        Mockito.when(controller.isOpen()).thenReturn(true);

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
    public void onMoveOpen() throws Exception
    {
        int offset = 15;
        Mockito.when(controller.isOpen()).thenReturn(true);

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
        Mockito.when(controller.getPlaybackEpisode()).thenReturn(episode);

        proxy.onPlaybackEvent(PlaybackEvent.PLAYING);

        Mockito.verify(notificationManager).notify(PlaybackServiceProxy.NotificationId, notificationPlaying);
        Mockito.verify(positionSaver).start(episode);
    }

    @Test
    public void onPlaybackEventPaused() throws Exception
    {
        Mockito.when(controller.getPlaybackEpisode()).thenReturn(episode);

        proxy.onPlaybackEvent(PlaybackEvent.PLAYING);
        proxy.onPlaybackEvent(PlaybackEvent.PAUSED);

        Mockito.verify(notificationManager).notify(PlaybackServiceProxy.NotificationId,
                                                   notificationPaused);
        Mockito.verify(positionSaver).stop(false);
    }

    @Test
    public void onPlaybackEventEnded() throws Exception
    {
        Mockito.when(controller.getPlaybackEpisode()).thenReturn(episode);

        proxy.onPlaybackEvent(PlaybackEvent.PLAYING);
        proxy.onPlaybackEvent(PlaybackEvent.ENDED);

        Mockito.verify(notificationManager).cancel(PlaybackServiceProxy.NotificationId);
        Mockito.verify(positionSaver).stop(true);
        Mockito.verify(proxy).onPlay();
    }
}
