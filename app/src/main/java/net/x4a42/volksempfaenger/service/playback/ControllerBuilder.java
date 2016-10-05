package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.PreferencesBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodePathResolver;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodePathResolverBuilder;
import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDaoWrapper;
import net.x4a42.volksempfaenger.misc.ConnectivityStatus;
import net.x4a42.volksempfaenger.misc.ConnectivityStatusBuilder;
import net.x4a42.volksempfaenger.preferences.PreferenceChangedEventReceiver;
import net.x4a42.volksempfaenger.preferences.PreferenceChangedEventReceiverBuilder;

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
        ConnectivityStatus        connectivityStatus         = new ConnectivityStatusBuilder().build(context);
        Preferences               preferences                = new PreferencesBuilder().build(context);

        PreferenceChangedEventReceiver preferenceChangedEventReceiver
                = new PreferenceChangedEventReceiverBuilder().build();

        Controller controller
                = new Controller(playbackEventBroadcaster,
                                 mediaPlayer,
                                 audioFocusManager,
                                 audioBecomingNoisyManager,
                                 episodePositionDao,
                                 pathResolver,
                                 connectivityStatus,
                                 preferences,
                                 preferenceChangedEventReceiver);

        mediaPlayer.setOnPreparedListener(controller);
        mediaPlayer.setOnCompletionListener(controller);
        audioFocusManager.setListener(controller);
        audioBecomingNoisyManager.setListener(controller);
        audioBecomingNoisyManager.start();
        preferenceChangedEventReceiver.setListener(controller);

        return controller;
    }
}
