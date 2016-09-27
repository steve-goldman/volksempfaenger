package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

import junit.framework.Assert;

import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.NavUtilsWrapper;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.ToastMaker;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.service.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.ui.SettingsActivity;
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
    @Mock PlaybackServiceIntentProvider    intentProvider;
    @Mock Intent                           playIntent;
    @Mock Uri                              episodeUrlUri;
    @Mock ToastMaker                       toastMaker;
    @Mock NavUtilsWrapper                  navUtilsWrapper;
    @Mock EpisodeSharer                    sharer;
    @Mock IntentBuilder                    intentBuilder;
    @Mock Intent                           websiteIntent;
    @Mock Intent                           settingsIntent;
    @Mock DownloadHelper                   downloadHelper;
    @Mock PlaybackServiceConnectionManager connectionManager;
    @Mock PlaybackServiceFacade            facade;
    @Mock Menu                             menu;
    @Mock MenuItem                         item;
    ViewEpisodeActivityProxy               proxy;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(intentProvider.getPlayIntent(episode)).thenReturn(playIntent);

        Mockito.when(intentBuilder.build(Intent.ACTION_VIEW, episodeUrlUri)).thenReturn(websiteIntent);
        Mockito.when(intentBuilder.build(activity, SettingsActivity.class)).thenReturn(settingsIntent);

        Mockito.when(connectionManager.getFacade()).thenReturn(facade);

        Mockito.when(fragmentManager.findFragmentById(R.id.nowplaying)).thenReturn(fragment);

        proxy = new ViewEpisodeActivityProxy(activity,
                                             episode,
                                             fragmentManager,
                                             optionsMenuManager,
                                             playbackEventReceiver,
                                             presenter,
                                             intentProvider,
                                             toastMaker,
                                             navUtilsWrapper,
                                             sharer,
                                             intentBuilder,
                                             downloadHelper,
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
        proxy.onOptionsItemSelected(item);

        Mockito.verify(optionsMenuManager).onOptionsItemSelected(item);
    }

    @Test
    public void onGetFacade() throws Exception
    {
        Assert.assertEquals(facade, connectionManager.getFacade());
    }

    //
    // OptionsMenuManager.Listener
    //

    @Test
    public void onPlay() throws Exception
    {
        proxy.onPlay();

        Mockito.verify(activity).startService(playIntent);
    }

    @Test
    public void onDownload() throws Exception
    {
        proxy.onDownload();

        Mockito.verify(downloadHelper).download();
    }

    @Test
    public void onShare() throws Exception
    {
        proxy.onShare();

        Mockito.verify(sharer).share();
    }

    @Test
    public void onDelete() throws Exception
    {
        proxy.onDelete();

        // TODO
    }

    @Test
    public void onMarkListened() throws Exception
    {
        proxy.onMarkListened();

        // TODO
    }

    @Test
    public void onMarkNew() throws Exception
    {
        proxy.onMarkNew();

        // TODO
    }

    @Test
    public void onWebsite() throws Exception
    {
        // TODO: mock

        proxy.onWebsite();

        //Mockito.verify(activity).startActivity(websiteIntent);
    }

    @Test
    public void onWebsiteNoUri() throws Exception
    {
        proxy.onWebsite();

        Mockito.verify(toastMaker).showTextShort("No website");
    }

    @Test
    public void onSettings() throws Exception
    {
        proxy.onSettings();

        Mockito.verify(activity).startActivity(settingsIntent);
    }

    @Test
    public void onHome() throws Exception
    {
        proxy.onHome();

        Mockito.verify(navUtilsWrapper).navigateUpFromSameTask();
    }

    //
    // PlaybackEventListener
    //

    @Test
    public void onPlaybackEvent()
    {
        proxy.onPlaybackEvent(PlaybackEvent.PLAYING);

        Mockito.verify(activity).invalidateOptionsMenu();
    }

}
