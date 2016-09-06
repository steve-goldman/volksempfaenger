package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class PlaybackControllerBuilder
{
    public PlaybackController build(Context context)
    {
        PlaybackEventActionMapper playbackEventActionMapper  = new PlaybackEventActionMapper();
        PlaybackEventBroadcaster  playbackEventBroadcaster   = new PlaybackEventBroadcaster(context, playbackEventActionMapper);
        MediaPlayer               mediaPlayer                = new MediaPlayer();
        AudioManager              audioManager               = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        AudioFocusManager         audioFocusManager          = new AudioFocusManager(audioManager);
        AudioBecomingNoisyManager audioBecomingNoisyManager  = new AudioBecomingNoisyManager(context);

        PlaybackController playbackController = new PlaybackController(playbackEventBroadcaster,
                                                                       mediaPlayer,
                                                                       audioFocusManager,
                                                                       audioBecomingNoisyManager);

        mediaPlayer.setOnPreparedListener(playbackController);
        mediaPlayer.setOnCompletionListener(playbackController);
        audioFocusManager.setListener(playbackController);
        audioBecomingNoisyManager.setListener(playbackController);
        audioBecomingNoisyManager.start();

        return playbackController;
    }
}
