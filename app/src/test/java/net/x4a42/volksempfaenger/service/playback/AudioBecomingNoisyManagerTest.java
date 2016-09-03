package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class AudioBecomingNoisyManagerTest
{
    private Context                            context  = Mockito.mock(Context.class);
    private AudioBecomingNoisyManager.Listener listener = Mockito.mock(AudioBecomingNoisyManager.Listener.class);
    private AudioBecomingNoisyManager          audioBecomingNoisyManager;

    @Before
    public void setup() throws Exception
    {
        audioBecomingNoisyManager = new AudioBecomingNoisyManager(context).setListener(listener);
    }

    @Test
    public void start() throws Exception
    {
        audioBecomingNoisyManager.start();

        Mockito.verify(context).registerReceiver(Mockito.eq(audioBecomingNoisyManager),
                                                 Mockito.any(IntentFilter.class));
    }

    @Test
    public void stop() throws Exception
    {
        audioBecomingNoisyManager.stop();

        Mockito.verify(context).unregisterReceiver(audioBecomingNoisyManager);
    }

    @Test
    public void testOnReceive() throws Exception
    {
        Intent intent = Mockito.mock(Intent.class);
        Mockito.when(intent.getAction()).thenReturn(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

        audioBecomingNoisyManager.onReceive(context, intent);

        Mockito.verify(listener).onAudioBecomingNoisy();
    }
}