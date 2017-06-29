package net.x4a42.volksempfaenger.service.playback;

import android.media.AudioManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AudioFocusManagerTest
{
    @Mock AudioManager               audioManager;
    @Mock AudioFocusManager.Listener listener;
    AudioFocusManager                audioFocusManager;

    @Before
    public void setUp() throws Exception
    {
        audioFocusManager = new AudioFocusManager(audioManager).setListener(listener);
        Mockito.when(audioManager.requestAudioFocus(Mockito.any(AudioManager.OnAudioFocusChangeListener.class),
                                                    Mockito.anyInt(),
                                                    Mockito.anyInt()))
               .thenReturn(AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
    }

    @Test
    public void requestFocus() throws Exception
    {
        // idempotent
        audioFocusManager.requestFocus();
        audioFocusManager.requestFocus();

        Mockito.verify(audioManager, Mockito.times(1))
               .requestAudioFocus(audioFocusManager,
                                  AudioManager.STREAM_MUSIC,
                                  AudioManager.AUDIOFOCUS_GAIN);
    }

    @Test
    public void abandonFocus() throws Exception
    {
        audioFocusManager.requestFocus();
        // idempotent
        audioFocusManager.abandonFocus();
        audioFocusManager.abandonFocus();

        Mockito.verify(audioManager, Mockito.times(1)).abandonAudioFocus(audioFocusManager);
    }

    @Test
    public void onAudioFocusGained() throws Exception
    {
        audioFocusManager.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN);

        Mockito.verify(listener).onAudioFocusGained();
    }

    @Test
    public void onAudioFocusLost() throws Exception
    {
        audioFocusManager.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS);

        Mockito.verify(listener).onAudioFocusLost();
    }

    @Test
    public void onAudioFocusLostTransiently() throws Exception
    {
        audioFocusManager.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT);

        Mockito.verify(listener).onAudioFocusLostTransiently();
    }

    @Test
    public void onAudioFocusLostTransientlyCanDuck() throws Exception
    {
        audioFocusManager.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK);

        Mockito.verify(listener).onAudioFocusLostTransiently();
    }
}
