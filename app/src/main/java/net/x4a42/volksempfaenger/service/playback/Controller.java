package net.x4a42.volksempfaenger.service.playback;

import android.media.MediaPlayer;

import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodePathResolver;
import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDaoWrapper;
import net.x4a42.volksempfaenger.event.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventBroadcaster;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventListener;
import net.x4a42.volksempfaenger.event.preferencechanged.PreferenceChangedEventListener;
import net.x4a42.volksempfaenger.event.preferencechanged.PreferenceChangedEventReceiver;
import net.x4a42.volksempfaenger.misc.ConnectivityStatus;

import java.io.IOException;

/*
    This is a helper class to PlaybackService that controls playback.  It communicates
    with listeners in two ways.  One, via the instance set in setListener, which is called
    inline.  Others may subscribe to broadcasts via PlaybackEventReceiver.
 */

class Controller implements MediaPlayer.OnPreparedListener,
                            MediaPlayer.OnCompletionListener,
                            AudioFocusManager.Listener,
                            AudioBecomingNoisyManager.Listener,
                            PlaybackPositionProvider,
                            PreferenceChangedEventListener
{
    public static final float FullVolume    = 1.0f;
    public static final float DuckedVolume  = 0.1f;

    private final PlaybackEventBroadcaster       playbackEventBroadcaster;
    private final MediaPlayer                    mediaPlayer;
    private final AudioFocusManager              audioFocusManager;
    private final AudioBecomingNoisyManager      audioBecomingNoisyManager;
    private final EpisodePositionDaoWrapper      episodePositionDao;
    private final EpisodePathResolver            pathResolver;
    private final ConnectivityStatus             connectivityStatus;
    private final Preferences                    preferences;
    private final PreferenceChangedEventReceiver preferenceChangedEventReceiver;
    private PlaybackEventListener                playbackEventListener;
    private Episode                              playbackEpisode;
    private boolean                              inTransientLoss;
    private boolean                              isPrepared;
    private boolean                              isStreaming;

    public Controller(PlaybackEventBroadcaster       playbackEventBroadcaster,
                      MediaPlayer                    mediaPlayer,
                      AudioFocusManager              audioFocusManager,
                      AudioBecomingNoisyManager      audioBecomingNoisyManager,
                      EpisodePositionDaoWrapper      episodePositionDao,
                      EpisodePathResolver            pathResolver,
                      ConnectivityStatus             connectivityStatus,
                      Preferences                    preferences,
                      PreferenceChangedEventReceiver preferenceChangedEventReceiver)
    {
        this.playbackEventBroadcaster       = playbackEventBroadcaster;
        this.mediaPlayer                    = mediaPlayer;
        this.audioFocusManager              = audioFocusManager;
        this.audioBecomingNoisyManager      = audioBecomingNoisyManager;
        this.episodePositionDao             = episodePositionDao;
        this.pathResolver                   = pathResolver;
        this.connectivityStatus             = connectivityStatus;
        this.preferences                    = preferences;
        this.preferenceChangedEventReceiver = preferenceChangedEventReceiver;
    }

    public Controller setListener(PlaybackEventListener playbackEventListener)
    {
        this.playbackEventListener = playbackEventListener;
        return this;
    }

    public Episode getPlaybackEpisode()
    {
        return playbackEpisode;
    }

    public boolean isPlaybackEpisodeOpen(Episode episode)
    {
        return playbackEpisode != null
                && playbackEpisode.get_id().equals(episode.get_id());
    }

    @Override
    public int getPosition()
    {
        return isPrepared ? mediaPlayer.getCurrentPosition() : 0;
    }

    public int getDuration()
    {
        return isPrepared ? mediaPlayer.getDuration() : 0;
    }

    public boolean isPlaying()
    {
        return mediaPlayer.isPlaying();
    }

    public boolean isOpen()
    {
        return playbackEpisode != null;
    }

    public void open(Episode episode)
    {
        if (isPlaybackEpisodeOpen(episode))
        {
            return;
        }

        if (playbackEpisode != null)
        {
            stop();
        }

        if (!pathResolver.resolvesToFile(episode))
        {
            if (preferences.streamWifiOnly() && !connectivityStatus.isWifiConnected())
            {
                return;
            }
            isStreaming = true;
            preferenceChangedEventReceiver.subscribe();
        }

        playbackEpisode = episode;
        try
        {
            mediaPlayer.setDataSource(pathResolver.resolveUrl(playbackEpisode));
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
        if (isPrepared)
        {
            mediaPlayer.stop();
            isPrepared = false;
        }

        if (isStreaming)
        {
            preferenceChangedEventReceiver.unsubscribe();
            isStreaming = false;
        }

        audioFocusManager.abandonFocus();
        mediaPlayer.reset();
        playbackEpisode = null;
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
        isPrepared = true;
        seekTo(episodePositionDao.getOrInsert(playbackEpisode).getPosition());
        play();
    }

    //
    // MediaPlayer.OnCompletionListener
    //

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        stop();
        callListeners(PlaybackEvent.ENDED);
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
    // PreferenceChangedEventListener
    //

    @Override
    public void onPreferenceChanged()
    {
        System.out.println("ON PREFERENCE CHANGED!");
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
