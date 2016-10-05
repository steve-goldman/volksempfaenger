package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import net.x4a42.volksempfaenger.data.entity.episode.EpisodePathResolver;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodePathResolverBuilder;
import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDaoWrapper;

class ControllerBuilder
{
    public Controller build(Context context)
    {
        PlaybackEventBroadcaster  playbackEventBroadcaster   = new PlaybackEventBroadcasterBuilder().build();
        MediaPlayer               mediaPlayer                = new MediaPlayer();
        AudioManager              audioManager               = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        AudioFocusManager         audioFocusManager          = new AudioFocusManager(audioManager);
        AudioBecomingNoisyManager audioBecomingNoisyManager  = new AudioBecomingNoisyManager(context);
        EpisodePositionDaoWrapper episodePositionDao         = new EpisodePositionDaoBuilder().build(context);
        EpisodePathResolver       pathResolver               = new EpisodePathResolverBuilder().build(context);

        Controller controller
                = new Controller(playbackEventBroadcaster,
                                 mediaPlayer,
                                 audioFocusManager,
                                 audioBecomingNoisyManager,
                                 episodePositionDao,
                                 pathResolver);

        mediaPlayer.setOnPreparedListener(controller);
        mediaPlayer.setOnCompletionListener(controller);
        audioFocusManager.setListener(controller);
        audioBecomingNoisyManager.setListener(controller);
        audioBecomingNoisyManager.start();

        return controller;
    }
}
