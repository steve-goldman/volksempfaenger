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
import net.x4a42.volksempfaenger.event.connectivitychanged.ConnectivityChangedEventReceiver;
import net.x4a42.volksempfaenger.event.connectivitychanged.ConnectivityChangedEventReceiverBuilder;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventBroadcaster;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventBroadcasterBuilder;
import net.x4a42.volksempfaenger.event.preferencechanged.PreferenceChangedEventReceiver;
import net.x4a42.volksempfaenger.event.preferencechanged.PreferenceChangedEventReceiverBuilder;
import net.x4a42.volksempfaenger.misc.ConnectivityStatus;
import net.x4a42.volksempfaenger.misc.ConnectivityStatusBuilder;

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

        ConnectivityChangedEventReceiver connectivityChangedEventReceiver
                = new ConnectivityChangedEventReceiverBuilder().build();

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
                                 connectivityChangedEventReceiver,
                                 preferences,
                                 preferenceChangedEventReceiver);

        mediaPlayer.setOnPreparedListener(controller);
        mediaPlayer.setOnCompletionListener(controller);
        audioFocusManager.setListener(controller);
        audioBecomingNoisyManager.setListener(controller);
        audioBecomingNoisyManager.start();
        connectivityChangedEventReceiver.setListener(controller);
        preferenceChangedEventReceiver.setListener(controller);

        return controller;
    }
}
