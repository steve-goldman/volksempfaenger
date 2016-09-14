package net.x4a42.volksempfaenger.service.playback;

import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MediaSessionManagerTest
{
    @Mock MediaSession          mediaSession;
    @Mock PlaybackEventReceiver eventReceiver;
    MediaSessionManager         mediaSessionManager;

    @Before
    public void setUp() throws Exception
    {
        mediaSessionManager = new MediaSessionManager(mediaSession, eventReceiver);
    }

    @Test
    public void destroy() throws Exception
    {
        mediaSessionManager.destroy();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Mockito.verify(mediaSession).release();
        }
        Mockito.verify(eventReceiver).unsubscribe();
    }

    @Test
    public void onPlaybackEventPlaying() throws Exception
    {
        mediaSessionManager.onPlaybackEvent(PlaybackEvent.PLAYING);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Mockito.verify(mediaSession).setPlaybackState(Mockito.any(PlaybackState.class));
        }
    }

    @Test
    public void onPlaybackEventPaused() throws Exception
    {
        mediaSessionManager.onPlaybackEvent(PlaybackEvent.PAUSED);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Mockito.verify(mediaSession).setPlaybackState(Mockito.any(PlaybackState.class));
        }
    }
}
