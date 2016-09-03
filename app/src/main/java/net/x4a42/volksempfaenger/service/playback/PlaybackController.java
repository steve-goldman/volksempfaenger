package net.x4a42.volksempfaenger.service.playback;

import android.media.MediaPlayer;

import java.io.IOException;

/*
    This is a helper class to PlaybackService that controls playback.  It communicates
    with listeners in two ways.  One, via the instance set in setListener, which is called
    inline.  Others may subscribe to broadcasts via PlaybackEventReceiver.
 */

public class PlaybackController implements MediaPlayer.OnPreparedListener,
                                           MediaPlayer.OnCompletionListener,
                                           AudioFocusManager.Listener,
                                           AudioBecomingNoisyManager.Listener,
                                           PlaybackPositionProvider
{
    public static final float FullVolume    = 1.0f;
    public static final float DuckedVolume  = 0.1f;

    private final PlaybackEventBroadcaster  playbackEventBroadcaster;
    private final MediaPlayer               mediaPlayer;
    private final AudioFocusManager         audioFocusManager;
    private final AudioBecomingNoisyManager audioBecomingNoisyManager;
    private PlaybackEventListener           playbackEventListener;
    private PlaybackItem                    playbackItem;
    private boolean                         inTransientLoss;

    public PlaybackController(PlaybackEventBroadcaster  playbackEventBroadcaster,
                              MediaPlayer               mediaPlayer,
                              AudioFocusManager         audioFocusManager,
                              AudioBecomingNoisyManager audioBecomingNoisyManager)
    {
        this.playbackEventBroadcaster  = playbackEventBroadcaster;
        this.mediaPlayer               = mediaPlayer;
        this.audioFocusManager         = audioFocusManager;
        this.audioBecomingNoisyManager = audioBecomingNoisyManager;
    }

    public PlaybackController setListener(PlaybackEventListener playbackEventListener)
    {
        this.playbackEventListener = playbackEventListener;
        return this;
    }

    public PlaybackItem getPlaybackItem()
    {
        return playbackItem;
    }

    public boolean isPlaybackItemOpen(PlaybackItem playbackItem)
    {
        return this.playbackItem != null
                && this.playbackItem.getEpisodeUri().equals(playbackItem.getEpisodeUri());
    }

    @Override
    public int getPosition()
    {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration()
    {
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying()
    {
        return mediaPlayer.isPlaying();
    }

    public boolean isOpen()
    {
        return playbackItem != null;
    }

    public void open(PlaybackItem playbackItem)
    {
        if (this.playbackItem != null
                && this.playbackItem.getEpisodeUri().equals(playbackItem.getEpisodeUri()))
        {
            return;
        }

        if (this.playbackItem != null)
        {
            stop();
        }

        this.playbackItem = playbackItem;
        try
        {
            mediaPlayer.setDataSource(playbackItem.getPath());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    public void play()
    {
        audioFocusManager.requestFocus();
        mediaPlayer.start();
        callListeners(PlaybackEvent.PLAYING);
    }

    public void pause()
    {
        mediaPlayer.pause();
        callListeners(PlaybackEvent.PAUSED);
    }

    public void stop()
    {
        mediaPlayer.stop();
        audioFocusManager.abandonFocus();
        mediaPlayer.reset();
        playbackItem = null;
    }

    public void seekTo(int position)
    {
        mediaPlayer.seekTo(position);
    }

    public void movePosition(int offset)
    {
        int position = Math.min(getDuration(), Math.max(0, offset + getPosition()));
        seekTo(position);
    }

    public void destroy()
    {
        audioBecomingNoisyManager.stop();
        mediaPlayer.release();

        // TODO: null out members to prevent reference cycles?
    }

    //
    // MediaPlayer.OnPreparedListener
    //

    @Override
    public void onPrepared(MediaPlayer mediaPlayer)
    {
        seekTo(playbackItem.getDurationListenedAtStart());
        play();
    }

    //
    // MediaPlayer.OnCompletionListener
    //

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        callListeners(PlaybackEvent.ENDED);
        stop();
    }

    //
    // AudioFocusManager.Listener
    //

    @Override
    public void onAudioFocusGained()
    {
        mediaPlayer.setVolume(FullVolume, FullVolume);
        if (inTransientLoss)
        {
            inTransientLoss = false;
            play();
        }
    }

    @Override
    public void onAudioFocusLost()
    {
        if (isPlaying())
        {
            pause();
        }
    }

    @Override
    public void onAudioFocusLostTransiently()
    {
        if (isPlaying())
        {
            pause();
            inTransientLoss = true;
        }
    }

    @Override
    public void onAudioFocusLostTransientlyCanDuck()
    {
        if (isPlaying())
        {
            mediaPlayer.setVolume(DuckedVolume, DuckedVolume);
        }
    }

    //
    // AudioBecomingNoisyManager.Listener
    //

    @Override
    public void onAudioBecomingNoisy()
    {
        if (isPlaying())
        {
            pause();
        }
    }

    //
    // helper methods
    //

    private void callListeners(PlaybackEvent playbackEvent)
    {
        if (playbackEventListener != null)
        {
            playbackEventListener.onPlaybackEvent(playbackEvent);
        }
        playbackEventBroadcaster.broadcast(playbackEvent);
    }

}
