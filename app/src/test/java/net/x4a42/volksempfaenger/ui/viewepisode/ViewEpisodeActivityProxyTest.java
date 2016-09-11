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
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.episode.EpisodeCursorLoader;
import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelper;
import net.x4a42.volksempfaenger.service.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.ui.SettingsActivity;
import net.x4a42.volksempfaenger.ui.nowplaying.NowPlayingFragment;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ViewEpisodeActivityProxyTest
{
    ViewEpisodeActivity              activity              = Mockito.mock(ViewEpisodeActivity.class);
    FragmentManager                  fragmentManager       = Mockito.mock(FragmentManager.class);
    NowPlayingFragment               fragment              = Mockito.mock(NowPlayingFragment.class);
    Uri                              episodeUri            = Mockito.mock(Uri.class);
    OptionsMenuManager               optionsMenuManager    = Mockito.mock(OptionsMenuManager.class);
    PlaybackEventReceiver            playbackEventReceiver = Mockito.mock(PlaybackEventReceiver.class);
    EpisodeCursorLoader              episodeCursorLoader   = Mockito.mock(EpisodeCursorLoader.class);
    EpisodeCursor                    episodeCursor         = Mockito.mock(EpisodeCursor.class);
    long                             episodeId             = 10;
    Presenter                        presenter             = Mockito.mock(Presenter.class);
    PlaybackServiceIntentProvider    intentProvider        = Mockito.mock(PlaybackServiceIntentProvider.class);
    Intent                           playIntent            = Mockito.mock(Intent.class);
    EpisodeDataHelper                episodeDataHelper     = Mockito.mock(EpisodeDataHelper.class);
    Uri                              episodeUrlUri         = Mockito.mock(Uri.class);
    ToastMaker                       toastMaker            = Mockito.mock(ToastMaker.class);
    NavUtilsWrapper                  navUtilsWrapper       = Mockito.mock(NavUtilsWrapper.class);
    EpisodeSharer                    sharer                = Mockito.mock(EpisodeSharer.class);
    IntentBuilder                    intentBuilder         = Mockito.mock(IntentBuilder.class);
    Intent                           websiteIntent         = Mockito.mock(Intent.class);
    Intent                           settingsIntent        = Mockito.mock(Intent.class);
    DownloadHelper                   downloadHelper        = Mockito.mock(DownloadHelper.class);
    PlaybackServiceConnectionManager connectionManager     = Mockito.mock(PlaybackServiceConnectionManager.class);
    PlaybackServiceFacade            facade                = Mockito.mock(PlaybackServiceFacade.class);
    Menu                             menu                  = Mockito.mock(Menu.class);
    MenuItem                         item                  = Mockito.mock(MenuItem.class);
    ViewEpisodeActivityProxy         proxy;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(intentProvider.getPlayIntent(episodeUri)).thenReturn(playIntent);

        Mockito.when(episodeCursor.getId()).thenReturn(episodeId);
        Mockito.when(episodeCursor.getUrlUri()).thenReturn(episodeUrlUri);

        Mockito.when(intentBuilder.build(Intent.ACTION_VIEW, episodeUrlUri)).thenReturn(websiteIntent);
        Mockito.when(intentBuilder.build(activity, SettingsActivity.class)).thenReturn(settingsIntent);

        Mockito.when(connectionManager.getFacade()).thenReturn(facade);

        Mockito.when(fragmentManager.findFragmentById(R.id.nowplaying)).thenReturn(fragment);

        proxy = new ViewEpisodeActivityProxy(activity,
                                             fragmentManager,
                                             episodeUri,
                                             optionsMenuManager,
                                             playbackEventReceiver,
                                             episodeCursorLoader,
                                             presenter,
                                             intentProvider,
                                             episodeDataHelper,
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
        Mockito.verify(episodeCursorLoader).init();
        Mockito.verify(presenter).onCreate();
        Mockito.verify(fragment).setEpisodeUri(episodeUri);
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
        proxy.onCursorLoaded(episodeCursor);
        proxy.onDownload();

        Mockito.verify(downloadHelper).download(episodeCursor);
    }

    @Test
    public void onShare() throws Exception
    {
        proxy.onCursorLoaded(episodeCursor);
        proxy.onShare();

        Mockito.verify(sharer).share(episodeCursor);
    }

    @Test
    public void onDelete() throws Exception
    {
        proxy.onCursorLoaded(episodeCursor);
        proxy.onDelete();

        Mockito.verify(episodeDataHelper).delete(episodeUri, episodeId);
    }

    @Test
    public void onMarkListened() throws Exception
    {
        proxy.onCursorLoaded(episodeCursor);
        proxy.onMarkListened();

        Mockito.verify(episodeDataHelper).markListened(episodeUri);
    }

    @Test
    public void onMarkNew() throws Exception
    {
        proxy.onCursorLoaded(episodeCursor);
        proxy.onMarkNew();

        Mockito.verify(episodeDataHelper).markNew(episodeUri);
    }

    @Test
    public void onWebsite() throws Exception
    {
        proxy.onCursorLoaded(episodeCursor);
        proxy.onWebsite();

        Mockito.verify(activity).startActivity(websiteIntent);
    }

    @Test
    public void onWebsiteNoUri() throws Exception
    {
        Mockito.when(episodeCursor.getUrlUri()).thenReturn(null);

        proxy.onCursorLoaded(episodeCursor);
        proxy.onWebsite();

        Mockito.verify(toastMaker).showTextShort("No website");
    }

    @Test
    public void onSettings() throws Exception
    {
        proxy.onCursorLoaded(episodeCursor);
        proxy.onSettings();

        Mockito.verify(activity).startActivity(settingsIntent);
    }

    @Test
    public void onHome() throws Exception
    {
        proxy.onCursorLoaded(episodeCursor);
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

    //
    // EpisodeCursorLoader.Listener
    //

    @Test
    public void onCursorLoaded()
    {
        proxy.onCursorLoaded(episodeCursor);

        Mockito.verify(activity).invalidateOptionsMenu();
        Mockito.verify(presenter).update(episodeCursor);
    }

    @Test
    public void onCursorReset()
    {
        proxy.onCursorReset();

        Mockito.verify(activity).invalidateOptionsMenu();
    }

}
