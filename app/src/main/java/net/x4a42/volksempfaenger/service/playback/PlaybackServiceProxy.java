package net.x4a42.volksempfaenger.service.playback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;

class PlaybackServiceProxy implements PlaybackEventListener, IntentParser.Listener
{
    public static final int                          NotificationId = 0x59d54313;
    private final BackgroundPositionSaver            positionSaver;
    private final Controller                         controller;
    private final IntentParser                       intentParser;
    private final MediaButtonReceiver                mediaButtonReceiver;
    private final MediaSessionManager                mediaSessionManager;
    private final NotificationManager                notificationManager;
    private final PlaybackNotificationBuilder        notificationBuilder;

    public PlaybackServiceProxy(BackgroundPositionSaver            positionSaver,
                                Controller                         controller,
                                IntentParser                       intentParser,
                                MediaButtonReceiver                mediaButtonReceiver,
                                MediaSessionManager                mediaSessionManager,
                                NotificationManager                notificationManager,
                                PlaybackNotificationBuilder        notificationBuilder)
    {
        this.positionSaver              = positionSaver;
        this.controller                 = controller;
        this.intentParser               = intentParser;
        this.mediaButtonReceiver        = mediaButtonReceiver;
        this.mediaSessionManager        = mediaSessionManager;
        this.notificationManager        = notificationManager;
        this.notificationBuilder        = notificationBuilder;
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
        controller.destroy();
        mediaButtonReceiver.destroy();
        if (mediaSessionManager != null)
        {
            mediaSessionManager.destroy();
        }
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
    public void onPlay(Episode episode)
    {
        if (controller.isPlaybackEpisodeOpen(episode))
        {
            if (!controller.isPlaying())
            {
                controller.play();
            }
        }
        else
        {
            controller.open(episode);
        }
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
        if (!controller.isOpen())
        {
            return;
        }

        if (controller.isPlaying())
        {
            controller.pause();
        }
        else
        {
            controller.play();
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

    private void handlePlaying()
    {
        updateNotification(true);
        positionSaver.start(controller.getPlaybackEpisode());
    }

    private void handlePaused()
    {
        updateNotification(false);
        positionSaver.stop(false);
    }

    private void handleEnded()
    {
        notificationManager.cancel(NotificationId);
        positionSaver.stop(true);
    }

    private void updateNotification(boolean isPlaying)
    {
        Notification notification = notificationBuilder.build(controller.getPlaybackEpisode(), isPlaying);
        notificationManager.notify(NotificationId, notification);
    }
}
