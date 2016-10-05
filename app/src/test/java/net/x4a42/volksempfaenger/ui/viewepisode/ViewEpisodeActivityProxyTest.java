package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.FragmentManager;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

import junit.framework.Assert;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.ui.nowplaying.NowPlayingFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ViewEpisodeActivityProxyTest
{
    @Mock ViewEpisodeActivity              activity;
    @Mock FragmentManager                  fragmentManager;
    @Mock NowPlayingFragment               fragment;
    @Mock Episode                          episode;
    @Mock OptionsMenuManager               optionsMenuManager;
    @Mock PlaybackEventReceiver            playbackEventReceiver;
    @Mock Presenter                        presenter;
    @Mock Uri                              episodeUrlUri;
    @Mock PlaybackServiceConnectionManager connectionManager;
    @Mock PlaybackServiceFacade            facade;
    @Mock Menu                             menu;
    @Mock MenuItem                         menuItem;
    ViewEpisodeActivityProxy               proxy;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(connectionManager.getFacade()).thenReturn(facade);

        Mockito.when(fragmentManager.findFragmentById(R.id.nowplaying)).thenReturn(fragment);

        proxy = new ViewEpisodeActivityProxy(activity,
                                             episode,
                                             fragmentManager,
                                             optionsMenuManager,
                                             playbackEventReceiver,
                                             presenter,
                                             connectionManager);
    }

    @Test
    public void onCreate() throws Exception
    {
        proxy.onCreate();

        Mockito.verify(activity).setContentView(R.layout.view_episode);
        Mockito.verify(connectionManager).onCreate();
        Mockito.verify(presenter).onCreate();
        Mockito.verify(fragment).setEpisode(episode);
    }

    @Test
    public void onResume() throws Exception
    {
        proxy.onResume();

        Mockito.verify(playbackEventReceiver).subscribe();
    }

    @Test
    public void onPause() throws Exception
    {
        proxy.onPause();

        Mockito.verify(playbackEventReceiver).unsubscribe();
    }

    @Test
    public void onDestroy() throws Exception
    {
        proxy.onDestroy();

        Mockito.verify(connectionManager).onDestroy();
    }

    @Test
    public void onCreateOptionsMenu() throws Exception
    {
        proxy.onCreateOptionsMenu(menu);

        Mockito.verify(optionsMenuManager).onCreateOptionsMenu(menu);
    }

    @Test
    public void onOptionsItemSelected() throws Exception
    {
        proxy.onOptionsItemSelected(menuItem);

        Mockito.verify(optionsMenuManager).onOptionsItemSelected(menuItem);
    }

    @Test
    public void onGetFacade() throws Exception
    {
        Assert.assertEquals(facade, connectionManager.getFacade());
    }
}
