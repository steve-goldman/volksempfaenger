package net.x4a42.volksempfaenger.service.playback;

import android.content.Intent;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class PlaybackServiceIntentProviderTest
{
    PlaybackServiceIntentFactory  intentFactory = Mockito.mock(PlaybackServiceIntentFactory.class);
    Intent                        createdIntent = Mockito.mock(Intent.class);
    PlaybackServiceIntentProvider intentProvider;

    @Before
    public void setUp() throws Exception
    {
        intentProvider = new PlaybackServiceIntentProvider(intentFactory);
        Mockito.when(intentFactory.create(Mockito.anyString())).thenReturn(createdIntent);
        Mockito.when(createdIntent.setData(Mockito.any(Uri.class))).thenReturn(createdIntent);
        Mockito.when(createdIntent.putExtra(Mockito.anyString(), Mockito.anyInt())).thenReturn(createdIntent);
    }

    @Test
    public void getPlayIntent() throws Exception
    {
        Uri    episodeUri = Mockito.mock(Uri.class);
        Intent intent     = intentProvider.getPlayIntent(episodeUri);

        Mockito.verify(intentFactory).create(PlaybackService.ActionPlay);
        Mockito.verify(createdIntent).setData(episodeUri);
        assertEquals(createdIntent, intent);
    }

    @Test
    public void getPauseIntent() throws Exception
    {
        Intent intent = intentProvider.getPauseIntent();

        Mockito.verify(intentFactory).create(PlaybackService.ActionPause);
        assertEquals(createdIntent, intent);
    }

    @Test
    public void getStopIntent() throws Exception
    {
        Intent intent = intentProvider.getStopIntent();

        Mockito.verify(intentFactory).create(PlaybackService.ActionStop);
        assertEquals(createdIntent, intent);
    }

    @Test
    public void getPlayPauseIntent() throws Exception
    {
        Intent intent = intentProvider.getPlayPauseIntent();

        Mockito.verify(intentFactory).create(PlaybackService.ActionPlayPause);
        assertEquals(createdIntent, intent);
    }

    @Test
    public void getSeekIntent() throws Exception
    {
        int    position = 10;
        Intent intent   = intentProvider.getSeekIntent(position);

        Mockito.verify(intentFactory).create(PlaybackService.ActionSeek);
        Mockito.verify(createdIntent).putExtra("position", position);
        assertEquals(createdIntent, intent);
    }

    @Test
    public void getMoveIntent() throws Exception
    {
        int    offset   = 10;
        Intent intent   = intentProvider.getMoveIntent(offset);

        Mockito.verify(intentFactory).create(PlaybackService.ActionMove);
        Mockito.verify(createdIntent).putExtra("offset", offset);
        assertEquals(createdIntent, intent);
    }

}