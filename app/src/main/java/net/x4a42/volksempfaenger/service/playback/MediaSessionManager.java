package net.x4a42.volksempfaenger.service.playback;

import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;

import net.x4a42.volksempfaenger.event.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventListener;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventReceiver;

class MediaSessionManager implements PlaybackEventListener
{
    private final MediaSession          mediaSession;
    private final PlaybackEventReceiver eventReceiver;

    public MediaSessionManager(MediaSession          mediaSession,
                               PlaybackEventReceiver eventReceiver)
    {
        this.mediaSession  = mediaSession;
        this.eventReceiver = eventReceiver;
    }

    public void destroy()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mediaSession.release();
        }
        eventReceiver.unsubscribe();
    }

    @Override
    public void onPlaybackEvent(PlaybackEvent playbackEvent)
    {
        switch (playbackEvent)
        {
            case PLAYING:
                handlePlaying();
                break;
            case PAUSED:
                handlePaused();
                break;
            default:
                break;
        }
    }

    private void handlePlaying()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mediaSession.setPlaybackState(getState(PlaybackState.STATE_PLAYING));
        }
    }

    private void handlePaused()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mediaSession.setPlaybackState(getState(PlaybackState.STATE_PAUSED));
        }
    }

    private PlaybackState getState(int state)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            return new PlaybackState.Builder()
                    .setState(state, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 1.0f)
                    .build();
        }
        throw new Error();
    }
}
