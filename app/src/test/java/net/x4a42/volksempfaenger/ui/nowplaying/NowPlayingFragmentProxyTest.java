package net.x4a42.volksempfaenger.ui.nowplaying;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import junit.framework.Assert;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.service.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NowPlayingFragmentProxyTest
{
    @Mock Uri                              episodeUri;
    @Mock Uri                              otherEpisodeUri;
    @Mock PlaybackServiceConnectionManager connectionManager;
    @Mock PlaybackServiceFacade            facade;
    @Mock PlaybackEventReceiver            playbackEventReceiver;
    @Mock NowPlayingFragment               fragment;
    @Mock SeekBarManager                   seekBarManager;
    @Mock ControlButtonsManager            controlButtonsManager;
    @Mock InfoSectionManager               infoSectionManager;
    @Mock LayoutInflater                   inflater;
    @Mock View                             view;
    @Mock ViewGroup                        containter;
    NowPlayingFragmentProxy                proxy;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(inflater.inflate(R.layout.nowplaying, containter, false)).thenReturn(view);

        proxy = new NowPlayingFragmentProxy(fragment,
                                            connectionManager,
                                            playbackEventReceiver,
                                            seekBarManager,
                                            controlButtonsManager,
                                            infoSectionManager);
        proxy.setEpisodeUri(episodeUri);
    }

    @Test
    public void onCreate() throws Exception
    {
        proxy.onCreate();

        Mockito.verify(connectionManager).onCreate();
    }

    @Test
    public void onCreateView() throws Exception
    {
        View createdView = proxy.onCreateView(inflater, containter);

        Mockito.verify(seekBarManager).onCreateView(createdView);
        Mockito.verify(controlButtonsManager).onCreateView(createdView);
        Mockito.verify(infoSectionManager).onCreateView(createdView);

        Assert.assertEquals(view, createdView);
    }

    @Test
    public void onStart() throws Exception
    {
        proxy.onStart();
    }

    @Test
    public void onResume() throws Exception
    {
        proxy.onResume();

        Mockito.verify(seekBarManager).onResume();
        Mockito.verify(controlButtonsManager).onResume();
        Mockito.verify(infoSectionManager).onResume();
    }

    @Test
    public void onPause() throws Exception
    {
        proxy.onPause();

        Mockito.verify(seekBarManager).onPause();
        Mockito.verify(controlButtonsManager).onPause();
        Mockito.verify(infoSectionManager).onPause();
    }

    @Test
    public void onDestroy() throws Exception
    {
        proxy.onDestroy();

        Mockito.verify(connectionManager).onDestroy();
    }

    @Test
    public void onPlaybackServiceConnectedNotOpen() throws Exception
    {
        Mockito.when(connectionManager.getFacade()).thenReturn(facade);
        Mockito.when(facade.isOpen()).thenReturn(false);

        proxy.onPlaybackServiceConnected();

        Mockito.verify(controlButtonsManager).onPlaybackServiceConnected();
        Mockito.verify(infoSectionManager).onPlaybackServiceConnected();
        Mockito.verify(seekBarManager).hide();
        Mockito.verify(controlButtonsManager).hide();
        Mockito.verify(infoSectionManager).hide();
    }

    @Test
    public void onPlaybackServiceConnectedOpenToSame() throws Exception
    {
        Mockito.when(connectionManager.getFacade()).thenReturn(facade);
        Mockito.when(facade.isOpen()).thenReturn(true);
        Mockito.when(facade.getEpisodeUri()).thenReturn(episodeUri);

        proxy.onPlaybackServiceConnected();

        Mockito.verify(controlButtonsManager).onPlaybackServiceConnected();
        Mockito.verify(infoSectionManager).onPlaybackServiceConnected();
        Mockito.verify(seekBarManager).show();
        Mockito.verify(controlButtonsManager).show();
        Mockito.verify(infoSectionManager).hide();
    }

    @Test
    public void onPlaybackServiceConnectedOpenToOther() throws Exception
    {
        Mockito.when(connectionManager.getFacade()).thenReturn(facade);
        Mockito.when(facade.isOpen()).thenReturn(true);
        Mockito.when(facade.getEpisodeUri()).thenReturn(otherEpisodeUri);

        proxy.onPlaybackServiceConnected();

        Mockito.verify(controlButtonsManager).onPlaybackServiceConnected();
        Mockito.verify(infoSectionManager).onPlaybackServiceConnected();
        Mockito.verify(seekBarManager).hide();
        Mockito.verify(controlButtonsManager).hide();
        Mockito.verify(infoSectionManager).show();
    }

    @Test
    public void onPlaybackServiceDisconnected() throws Exception
    {
        proxy.onPlaybackServiceDisconnected();

        Mockito.verify(controlButtonsManager).onPlaybackServiceDisconnected();
        Mockito.verify(infoSectionManager).onPlaybackServiceDisconnected();
        Mockito.verify(seekBarManager).hide();
        Mockito.verify(controlButtonsManager).hide();
        Mockito.verify(infoSectionManager).hide();
    }


    @Test
    public void onPlaybackEvent() throws Exception
    {
        proxy.onPlaybackEvent(PlaybackEvent.PLAYING);
    }
}
