package net.x4a42.volksempfaenger.ui.nowplaying;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.service.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacadeProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ControlButtonsManagerTest
{
    @Mock Context                          context;
    @Mock PlaybackEventReceiver            playbackEventReceiver;
    @Mock PlaybackServiceIntentProvider    intentProvider;
    @Mock Intent                           playPauseIntent;
    @Mock Intent                           rewindIntent;
    @Mock Intent                           fastForwardIntent;
    @Mock Intent                           nextIntent;
    @Mock Intent                           skipIntent;
    @Mock View                             view;
    @Mock LinearLayout                     controlsLayout;
    @Mock ImageButton                      rewindButton;
    @Mock ImageButton                      playPauseButton;
    @Mock ImageButton                      fastForwardButton;
    @Mock ImageButton                      nextButton;
    @Mock ImageButton                      skipButton;
    @Mock PlaybackServiceFacadeProvider    facadeProvider;
    @Mock PlaybackServiceFacade            facade;
    int                                    offset                = 20;
    ControlButtonsManager                  controlButtonsManager;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(view.findViewById(R.id.controls)).thenReturn(controlsLayout);
        Mockito.when(view.findViewById(R.id.back)).thenReturn(rewindButton);
        Mockito.when(view.findViewById(R.id.pause)).thenReturn(playPauseButton);
        Mockito.when(view.findViewById(R.id.forward)).thenReturn(fastForwardButton);
        Mockito.when(view.findViewById(R.id.next)).thenReturn(nextButton);
        Mockito.when(view.findViewById(R.id.skip)).thenReturn(skipButton);
        Mockito.when(rewindButton.getContext()).thenReturn(context);
        Mockito.when(playPauseButton.getContext()).thenReturn(context);
        Mockito.when(fastForwardButton.getContext()).thenReturn(context);
        Mockito.when(nextButton.getContext()).thenReturn(context);
        Mockito.when(skipButton.getContext()).thenReturn(context);
        Mockito.when(intentProvider.getPlayPauseIntent()).thenReturn(playPauseIntent);
        Mockito.when(intentProvider.getMoveIntent(-offset)).thenReturn(rewindIntent);
        Mockito.when(intentProvider.getMoveIntent(offset)).thenReturn(fastForwardIntent);
        Mockito.when(intentProvider.getNextIntent()).thenReturn(nextIntent);
        Mockito.when(intentProvider.getSkipIntent()).thenReturn(skipIntent);
        Mockito.when(facadeProvider.getFacade()).thenReturn(facade);

        controlButtonsManager = Mockito.spy(
                new ControlButtonsManager(playbackEventReceiver,
                                          intentProvider,
                                          offset)
                        .setFacadeProvider(facadeProvider));
    }

    @Test
    public void onCreateView() throws Exception
    {
        controlButtonsManager.onCreateView(view);
    }

    @Test
    public void hide() throws Exception
    {
        controlButtonsManager.onCreateView(view);
        controlButtonsManager.hide();

        Mockito.verify(controlsLayout).setVisibility(View.GONE);
    }

    @Test
    public void show() throws Exception
    {
        controlButtonsManager.onCreateView(view);
        controlButtonsManager.show();

        Mockito.verify(controlsLayout).setVisibility(View.VISIBLE);
    }

    @Test
    public void onResume() throws Exception
    {
        controlButtonsManager.onResume();

        Mockito.verify(playbackEventReceiver).subscribe();
    }

    @Test
    public void onPause() throws Exception
    {
        controlButtonsManager.onPause();

        Mockito.verify(playbackEventReceiver).unsubscribe();
    }

    @Test
    public void onPlaybackEventPlaying() throws Exception
    {
        controlButtonsManager.onCreateView(view);
        controlButtonsManager.onPlaybackEvent(PlaybackEvent.PLAYING);

        InOrder inOrder = Mockito.inOrder(playPauseButton, rewindButton, fastForwardButton, controlButtonsManager);
        inOrder.verify(playPauseButton).setImageResource(R.drawable.ic_media_play);
        inOrder.verify(playPauseButton).setImageResource(R.drawable.ic_media_pause);
    }

    @Test
    public void onPlaybackEventPaused() throws Exception
    {
        controlButtonsManager.onCreateView(view);
        controlButtonsManager.onPlaybackEvent(PlaybackEvent.PAUSED);

        Mockito.verify(playPauseButton, Mockito.times(2)).setImageResource(R.drawable.ic_media_play);
    }

    @Test
    public void onPlaybackEventEnded() throws Exception
    {
        controlButtonsManager.onCreateView(view);
        controlButtonsManager.onPlaybackEvent(PlaybackEvent.ENDED);

        Mockito.verify(playPauseButton, Mockito.times(2)).setImageResource(R.drawable.ic_media_play);
    }

    @Test
    public void onClickRewind() throws Exception
    {
        controlButtonsManager.onCreateView(view);
        controlButtonsManager.onClick(rewindButton);

        Mockito.verify(context).startService(rewindIntent);
    }

    @Test
    public void onClickPlayPause() throws Exception
    {
        controlButtonsManager.onCreateView(view);
        controlButtonsManager.onClick(playPauseButton);

        Mockito.verify(context).startService(playPauseIntent);
    }

    @Test
    public void onClickFastForward() throws Exception
    {
        controlButtonsManager.onCreateView(view);
        controlButtonsManager.onClick(fastForwardButton);

        Mockito.verify(context).startService(fastForwardIntent);
    }

    @Test
    public void onNext() throws Exception
    {
        controlButtonsManager.onCreateView(view);
        controlButtonsManager.onClick(nextButton);

        Mockito.verify(context).startService(nextIntent);
    }

    @Test
    public void onSkip() throws Exception
    {
        controlButtonsManager.onCreateView(view);
        controlButtonsManager.onClick(skipButton);

        Mockito.verify(context).startService(skipIntent);
    }

    //
    // PlaybackServiceConnectionManager.Listener
    //

    @Test
    public void onPlaybackServiceConnectedNotPlaying() throws Exception
    {
        controlButtonsManager.onCreateView(view);
        controlButtonsManager.onPlaybackServiceConnected();

        InOrder inOrder = Mockito.inOrder(playPauseButton, rewindButton, fastForwardButton);
        inOrder.verify(playPauseButton).setImageResource(R.drawable.ic_media_play);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void onPlaybackServiceConnectedPlaying() throws Exception
    {
        Mockito.when(facade.isPlaying()).thenReturn(true);

        controlButtonsManager.onCreateView(view);
        controlButtonsManager.onPlaybackServiceConnected();

        InOrder inOrder = Mockito.inOrder(playPauseButton, rewindButton, fastForwardButton);
        inOrder.verify(playPauseButton).setImageResource(R.drawable.ic_media_play);
        inOrder.verify(playPauseButton).setImageResource(R.drawable.ic_media_pause);
    }

    @Test
    public void onPlaybackServiceDisconnected() throws Exception
    {
        controlButtonsManager.onCreateView(view);
        controlButtonsManager.onPlaybackServiceDisconnected();

        Mockito.verify(playPauseButton, Mockito.times(2)).setImageResource(R.drawable.ic_media_play);
    }
}
