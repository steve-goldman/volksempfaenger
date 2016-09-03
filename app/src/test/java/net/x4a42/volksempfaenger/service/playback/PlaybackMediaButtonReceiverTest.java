package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PlaybackMediaButtonReceiverTest
{
    Context                       context        = Mockito.mock(Context.class);
    PlaybackServiceIntentProvider intentProvider = Mockito.mock(PlaybackServiceIntentProvider.class);
    Intent                        intent         = Mockito.mock(Intent.class);
    KeyEvent                      event          = Mockito.mock(KeyEvent.class);
    Intent                        serviceIntent  = Mockito.mock(Intent.class);
    PlaybackMediaButtonReceiver   mediaButtonReceiver;

    @Before
    public void setUp() throws Exception
    {
        mediaButtonReceiver = new PlaybackMediaButtonReceiver(context, intentProvider);
    }

    @Test
    public void destroy() throws Exception
    {
        mediaButtonReceiver.destroy();

        Mockito.verify(context).unregisterReceiver(mediaButtonReceiver);
    }

    @Test
    public void onReceiveNoAction() throws Exception
    {
        mediaButtonReceiver.onReceive(context, intent);

        Mockito.verifyNoMoreInteractions(context);
    }

    @Test
    public void onReceiveWrongAction() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn("my-wrong-action");

        mediaButtonReceiver.onReceive(context, intent);

        Mockito.verifyNoMoreInteractions(context);
    }

    @Test
    public void onReceiveNoEvent() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(Intent.ACTION_MEDIA_BUTTON);
        Mockito.when(intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)).thenReturn(null);

        mediaButtonReceiver.onReceive(context, intent);

        Mockito.verifyNoMoreInteractions(context);
    }

    @Test
    public void onReceiveWrongEventAction() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(Intent.ACTION_MEDIA_BUTTON);
        Mockito.when(intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)).thenReturn(event);
        Mockito.when(event.getAction()).thenReturn(KeyEvent.ACTION_UP - 1);

        mediaButtonReceiver.onReceive(context, intent);

        Mockito.verifyNoMoreInteractions(context);
    }

    @Test
    public void onReceiveWrongKeyCode() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(Intent.ACTION_MEDIA_BUTTON);
        Mockito.when(intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)).thenReturn(event);
        Mockito.when(event.getAction()).thenReturn(KeyEvent.ACTION_UP);
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE - 1);

        mediaButtonReceiver.onReceive(context, intent);

        Mockito.verifyNoMoreInteractions(context);
    }

    @Test
    public void onReceive() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(Intent.ACTION_MEDIA_BUTTON);
        Mockito.when(intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)).thenReturn(event);
        Mockito.when(event.getAction()).thenReturn(KeyEvent.ACTION_UP);
        Mockito.when(event.getKeyCode()).thenReturn(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
        Mockito.when(intentProvider.getPlayPauseIntent()).thenReturn(serviceIntent);

        mediaButtonReceiver.onReceive(context, intent);

        Mockito.verify(context).startService(serviceIntent);
    }
}