package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

class ControllerBuilder
{
    public Controller build(Context context)
    {
        PlaybackEventBroadcaster  playbackEventBroadcaster   = new PlaybackEventBroadcasterBuilder().build();
        MediaPlayer               mediaPlayer                = new MediaPlayer();
        AudioManager              audioManager               = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        AudioFocusManager         audioFocusManager          = new AudioFocusManager(audioManager);
        AudioBecomingNoisyManager audioBecomingNoisyManager  = new AudioBecomingNoisyManager(context);

        Controller controller
                = new Controller(playbackEventBroadcaster,
                                 mediaPlayer,
                                 audioFocusManager,
                                 audioBecomingNoisyManager);

        mediaPlayer.setOnPreparedListener(controller);
        mediaPlayer.setOnCompletionListener(controller);
        audioFocusManager.setListener(controller);
        audioBecomingNoisyManager.setListener(controller);
        audioBecomingNoisyManager.start();

        return controller;
    }
}
