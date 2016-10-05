package net.x4a42.volksempfaenger.service.playback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.playlist.Playlist;
import net.x4a42.volksempfaenger.service.playlistdownload.EpisodeDownloadEvent;
import net.x4a42.volksempfaenger.service.playlistdownload.EpisodeDownloadEventListener;
import net.x4a42.volksempfaenger.service.playlistdownload.EpisodeDownloadEventReceiver;

class PlaybackServiceProxy implements PlaybackEventListener, IntentParser.Listener, EpisodeDownloadEventListener
{
    public static final int                          NotificationId = 0x59d54313;
    private final BackgroundPositionSaver            positionSaver;
    private final Controller                         controller;
    private final IntentParser                       intentParser;
    private final MediaButtonReceiver                mediaButtonReceiver;
    private final MediaSessionManager                mediaSessionManager;
    private final NotificationManager                notificationManager;
    private final PlaybackNotificationBuilder        notificationBuilder;
    private final Playlist                           playlist;
    private final EpisodeDownloadEventReceiver       episodeDownloadEventReceiver;

    public PlaybackServiceProxy(BackgroundPositionSaver            positionSaver,
                                Controller                         controller,
                                IntentParser                       intentParser,
                                MediaButtonReceiver                mediaButtonReceiver,
                                MediaSessionManager                mediaSessionManager,
                                NotificationManager                notificationManager,
                                PlaybackNotificationBuilder        notificationBuilder,
                                Playlist                           playlist,
                                EpisodeDownloadEventReceiver       episodeDownloadEventReceiver)
    {
        this.positionSaver                = positionSaver;
        this.controller                   = controller;
        this.intentParser                 = intentParser;
        this.mediaButtonReceiver          = mediaButtonReceiver;
        this.mediaSessionManager          = mediaSessionManager;
        this.notificationManager          = notificationManager;
        this.notificationBuilder          = notificationBuilder;
        this.playlist                     = playlist;
        this.episodeDownloadEventReceiver = episodeDownloadEventReceiver;
    }

    public void onCreate()
    {
        episodeDownloadEventReceiver.subscribe();
    }

    public int onStartCommand(Intent intent)
    {
        // these come from app elements that want to change the state of the player
        // by either playing, pausing, or stopping

        intentParser.parse(intent);

        return Service.START_STICKY;
    }

    public void onDestroy()
    {
        episodeDownloadEventReceiver.unsubscribe();
        controller.destroy();
        mediaButtonReceiver.destroy();
        if (mediaSessionManager != null)
        {
            mediaSessionManager.destroy();
        }
        playlist.setPlaying(false);
    }

    public IBinder onBind()
    {
        return new PlaybackServiceBinder(new PlaybackServiceFacade(controller));
    }

    @Override
    public void onPlaybackEvent(PlaybackEvent playbackEvent)
    {
        // these come from our own controller to let us know that it has changed state.
        switch (playbackEvent)
        {
            case PLAYING:
                handlePlaying();
                break;
            case PAUSED:
                handlePaused();
                break;
            case ENDED:
                handleEnded();
                break;
            default:
                // we ignore these
                break;
        }
    }

    @Override
    public void onPlay()
    {
        if (playlist.isEmpty())
        {
            return;
        }

        Episode episode = playlist.getCurrentEpisode();

        if (controller.isPlaybackEpisodeOpen(episode))
        {
            if (!controller.isPlaying())
            {
                controller.play();
            }
            return;
        }

        positionSaver.stop(false);
        controller.open(episode);
    }

    @Override
    public void onPause()
    {
        if (controller.isPlaying())
        {
            controller.pause();
        }
    }

    @Override
    public void onPlayPause()
    {
        if (controller.isOpen() && controller.isPlaying())
        {
            controller.pause();
        }
        else
        {
            onPlay();
        }
    }

    @Override
    public void onStop()
    {
        onPause();
        controller.stop();
        notificationManager.cancel(NotificationId);
    }

    @Override
    public void onSeek(int position)
    {
        if (controller.isOpen())
        {
            controller.seekTo(position);
        }
    }

    @Override
    public void onMove(int offset)
    {
        if (controller.isOpen())
        {
            controller.movePosition(offset);
        }
    }

    @Override
    public void onNext()
    {
        // same behavior as if the episode ended
        handleEnded();
    }

    @Override
    public void onSkip()
    {
        notificationManager.cancel(NotificationId);
        positionSaver.stop(false);
        playlist.episodeSkipped();
        onPlay();
    }

    @Override
    public void onEpisodeDownloadEvent(EpisodeDownloadEvent event)
    {
        if (!controller.isPlaybackEpisodeOpen(event.getEpisode()))
        {
            return;
        }

        notificationManager.cancel(NotificationId);
        positionSaver.stop(false);
        controller.stop();
        onPlay();
    }

    private void handlePlaying()
    {
        updateNotification(true);
        positionSaver.start(controller.getPlaybackEpisode());
        playlist.setPlaying(true);
    }

    private void handlePaused()
    {
        updateNotification(false);
        positionSaver.stop(false);
        playlist.setPlaying(false);
    }

    private void handleEnded()
    {
        notificationManager.cancel(NotificationId);
        positionSaver.stop(true);
        playlist.episodeEnded();
        onPlay();
    }

    private void updateNotification(boolean isPlaying)
    {
        Notification notification = notificationBuilder.build(controller.getPlaybackEpisode(), isPlaying);
        notificationManager.notify(NotificationId, notification);
    }
}
