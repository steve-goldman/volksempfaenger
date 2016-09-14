package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AudioBecomingNoisyManagerTest
{
    @Mock private Context                            context;
    @Mock private AudioBecomingNoisyManager.Listener listener;
    AudioBecomingNoisyManager                        audioBecomingNoisyManager;

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
