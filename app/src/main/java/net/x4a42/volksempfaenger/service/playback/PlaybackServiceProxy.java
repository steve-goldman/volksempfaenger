package net.x4a42.volksempfaenger.service.playback;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelper;

public class PlaybackServiceProxy implements PlaybackEventListener, PlaybackServiceIntentParser.Listener
{
    private final PlaybackService                    playbackService;
    private final PlaybackBackgroundPositionSaver    positionSaver;
    private final PlaybackController                 controller;
    private final PlaybackItemBuilder                playbackItemBuilder;
    private final PlaybackServiceIntentParser        intentParser;
    private final EpisodeDataHelper episodeDataHelper;
    private final PlaybackMediaButtonReceiver        mediaButtonReceiver;
    private final MediaSessionManager                mediaSessionManager;
    private final PlaybackNotificationManagerBuilder notificationManagerBuilder;
    private PlaybackNotificationManager              notificationManager;

    public PlaybackServiceProxy(PlaybackService                    playbackService,
                                PlaybackBackgroundPositionSaver    positionSaver,
                                PlaybackController                 controller,
                                PlaybackItemBuilder                playbackItemBuilder,
                                PlaybackServiceIntentParser        intentParser,
                                EpisodeDataHelper                  episodeDataHelper,
                                PlaybackMediaButtonReceiver        mediaButtonReceiver,
                                MediaSessionManager                mediaSessionManager,
                                PlaybackNotificationManagerBuilder notificationManagerBuilder)
    {
        this.playbackService            = playbackService;
        this.positionSaver              = positionSaver;
        this.controller                 = controller;
        this.playbackItemBuilder        = playbackItemBuilder;
        this.intentParser               = intentParser;
        this.episodeDataHelper          = episodeDataHelper;
        this.mediaButtonReceiver        = mediaButtonReceiver;
        this.mediaSessionManager        = mediaSessionManager;
        this.notificationManagerBuilder = notificationManagerBuilder;
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
    public void onPlay(Uri episodeUri)
    {
        PlaybackItem playbackItem = playbackItemBuilder.build(playbackService,
                                                              getEpisodeUri(episodeUri));

        if (controller.isPlaybackItemOpen(playbackItem))
        {
            if (!controller.isPlaying())
            {
                controller.play();
            }
        }
        else
        {
            controller.open(playbackItem);
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
        notificationManager.remove();
    }

    @Override
    public void onSeek(int position)
    {
        if (controller.isPlaying())
        {
            controller.seekTo(position);
        }
    }

    @Override
    public void onMove(int offset)
    {
        if (controller.isPlaying())
        {
            controller.movePosition(offset);
        }
    }

    private void handlePlaying()
    {
        notificationManager = notificationManagerBuilder.build(playbackService, controller.getPlaybackItem());
        notificationManager.updateForPlay();
        positionSaver.start(controller.getPlaybackItem(), controller);
        episodeDataHelper.markListening(controller.getPlaybackItem().getEpisodeUri());
    }

    private void handlePaused()
    {
        notificationManager.updateForPause(controller.getPlaybackItem());
        positionSaver.stop();
    }

    private void handleEnded()
    {
        notificationManager.remove();
        positionSaver.stop();
    }

    private Uri getEpisodeUri(Uri episodeUri)
    {
        if (episodeUri == null && controller.getPlaybackItem() != null)
        {
            return controller.getPlaybackItem().getEpisodeUri();
        }
        return episodeUri;
    }
}
