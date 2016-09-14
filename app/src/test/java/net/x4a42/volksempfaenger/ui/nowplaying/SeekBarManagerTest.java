package net.x4a42.volksempfaenger.ui.nowplaying;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.RepeatingIntervalTimer;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacadeProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SeekBarManagerTest
{
    @Mock Context                          context;
    @Mock RepeatingIntervalTimer           repeatingIntervalTimer;
    @Mock PlaybackServiceIntentProvider    intentProvider;
    @Mock Intent                           seekIntent;
    @Mock View                             view;
    @Mock LinearLayout                     seekBarLayout;
    @Mock SeekBar                          seekBar;
    @Mock TextView                         positionText;
    @Mock TextView                         durationText;
    @Mock PlaybackServiceFacade            facade;
    @Mock PlaybackServiceFacadeProvider    facadeProvider;
    int                                    position               = 10;
    int                                    duration               = 20;
    int                                    progress               = 42;
    SeekBarManager                         seekBarManager;
    
    @Before
    public void setUp() throws Exception
    {
        Mockito.when(facadeProvider.getFacade()).thenReturn(facade);
        Mockito.when(facade.getPosition()).thenReturn(position);
        Mockito.when(facade.getDuration()).thenReturn(duration);
        Mockito.when(view.findViewById(R.id.progress_display)).thenReturn(seekBarLayout);
        Mockito.when(view.findViewById(R.id.seekbar)).thenReturn(seekBar);
        Mockito.when(view.findViewById(R.id.position)).thenReturn(positionText);
        Mockito.when(view.findViewById(R.id.duration)).thenReturn(durationText);
        Mockito.when(seekBar.getContext()).thenReturn(context);
        Mockito.when(intentProvider.getSeekIntent(progress)).thenReturn(seekIntent);

        seekBarManager = new SeekBarManager(repeatingIntervalTimer,
                                            intentProvider)
                .setFacadeProvider(facadeProvider);
    }

    @Test
    public void onCreateView() throws Exception
    {
        seekBarManager.onCreateView(view);
    }

    @Test
    public void hide() throws Exception
    {
        seekBarManager.onCreateView(view);
        seekBarManager.hide();

        Mockito.verify(seekBarLayout).setVisibility(View.GONE);
    }

    @Test
    public void show() throws Exception
    {
        seekBarManager.onCreateView(view);
        seekBarManager.show();

        Mockito.verify(seekBarLayout).setVisibility(View.VISIBLE);
    }

    @Test
    public void onResume() throws Exception
    {
        seekBarManager.onResume();

        Mockito.verify(repeatingIntervalTimer).start();
    }

    @Test
    public void onPause() throws Exception
    {
        seekBarManager.onPause();

        Mockito.verify(repeatingIntervalTimer).stop();
    }

    //
    // SeekBar.OnSeekBarChangeListener
    //

    @Test
    public void onProgressChangedFromUser() throws Exception
    {
        seekBarManager.onProgressChanged(seekBar, progress, true);

        Mockito.verify(context).startService(seekIntent);
        Mockito.verify(repeatingIntervalTimer).start();
    }

    @Test
    public void onProgressChangedNotFromUser() throws Exception
    {
        seekBarManager.onProgressChanged(seekBar, progress, false);

        Mockito.verifyNoMoreInteractions(context, repeatingIntervalTimer);
    }

    @Test
    public void onStartTrackingTouch() throws Exception
    {
        seekBarManager.onStartTrackingTouch(seekBar);

        Mockito.verify(repeatingIntervalTimer).stop();
    }

    @Test
    public void onStopTrackingTouch() throws Exception
    {
        seekBarManager.onStopTrackingTouch(seekBar);

        // nothing to test
    }

    @Test
    public void testRun() throws Exception
    {
        seekBarManager.onCreateView(view);
        seekBarManager.run();

        Mockito.verify(seekBar).setMax(duration);
        Mockito.verify(seekBar).setProgress(position);
        Mockito.verify(positionText).setText(Utils.formatTime(position));
        Mockito.verify(durationText).setText(Utils.formatTime(duration));
    }
}
