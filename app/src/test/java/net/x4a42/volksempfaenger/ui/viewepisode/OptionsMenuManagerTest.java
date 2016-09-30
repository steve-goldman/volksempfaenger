package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.x4a42.volksempfaenger.NavUtilsWrapper;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.ToastMaker;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.playlist.Playlist;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacadeProvider;
import net.x4a42.volksempfaenger.ui.settings.SettingsActivityIntentProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OptionsMenuManagerTest
{
    @Mock Activity                       activity;
    @Mock Episode                        episode;
    @Mock EpisodeSharer                  episodeSharer;
    @Mock SettingsActivityIntentProvider settingsIntentProvider;
    @Mock Intent                         settingsIntent;
    @Mock NavUtilsWrapper                navUtilsWrapper;
    @Mock Episode                        otherEpisode;
    @Mock PlaybackServiceFacadeProvider  facadeProvider;
    @Mock PlaybackServiceFacade          facade;
    @Mock MenuInflater                   inflater;
    @Mock Playlist                       playlist;
    @Mock Menu                           menu;
    @Mock MenuItem                       playItem;
    @Mock MenuItem                       shareItem;
    @Mock MenuItem                       settingsItem;
    @Mock MenuItem                       homeItem;
    @Mock ToastMaker                     toastMaker;
    OptionsMenuManager                   optionsMenuManager;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(facadeProvider.getFacade()).thenReturn(facade);
        Mockito.when(menu.findItem(R.id.item_play)).thenReturn(playItem);
        Mockito.when(playItem.getItemId()).thenReturn(R.id.item_play);
        Mockito.when(shareItem.getItemId()).thenReturn(R.id.item_share);
        Mockito.when(settingsItem.getItemId()).thenReturn(R.id.item_settings);
        Mockito.when(homeItem.getItemId()).thenReturn(android.R.id.home);
        Mockito.when(settingsIntentProvider.get()).thenReturn(settingsIntent);

        optionsMenuManager
                = new OptionsMenuManager(activity,
                                         episode,
                                         inflater,
                                         episodeSharer,
                                         settingsIntentProvider,
                                         navUtilsWrapper,
                                         playlist,
                                         toastMaker)
                .setFacadeProvider(facadeProvider);
    }

    @Test
    public void onCreateNotPlaying() throws Exception
    {
        optionsMenuManager.onCreateOptionsMenu(menu);

        Mockito.verify(playItem).setVisible(true);
    }

    @Test
    public void onCreatePlayingOtherEpisode() throws Exception
    {
        Mockito.when(facade.isPlaying()).thenReturn(true);
        Mockito.when(facade.getEpisode()).thenReturn(otherEpisode);

        optionsMenuManager.onCreateOptionsMenu(menu);

        Mockito.verify(playItem).setVisible(true);
    }

    @Test
    public void onCreatePlayingSameEpisode() throws Exception
    {
        Mockito.when(facade.isPlaying()).thenReturn(true);
        Mockito.when(facade.isEpisodeOpen(episode)).thenReturn(true);

        optionsMenuManager.onCreateOptionsMenu(menu);

        Mockito.verify(playItem).setVisible(false);
    }

    @Test
    public void onCreateNotDownloaded() throws Exception
    {
        optionsMenuManager.onCreateOptionsMenu(menu);

        // TODO
    }

    @Test
    public void onPlaySucceeds()
    {
        Mockito.when(playlist.playEpisodeNow(episode)).thenReturn(true);
        optionsMenuManager.onOptionsItemSelected(playItem);
        Mockito.verify(playlist).playEpisodeNow(episode);
        Mockito.verifyNoMoreInteractions(toastMaker);
    }

    @Test
    public void onPlayDoesNotSucceed()
    {
        optionsMenuManager.onOptionsItemSelected(playItem);
        Mockito.verify(playlist).playEpisodeNow(episode);
        Mockito.verify(toastMaker).showTextShort(Mockito.anyString());
    }

    @Test
    public void onShare()
    {
        optionsMenuManager.onOptionsItemSelected(shareItem);
        Mockito.verify(episodeSharer).share();
    }

    @Test
    public void onSettings()
    {
        optionsMenuManager.onOptionsItemSelected(settingsItem);
        Mockito.verify(activity).startActivity(settingsIntent);
    }

    @Test
    public void onHome()
    {
        optionsMenuManager.onOptionsItemSelected(homeItem);
        Mockito.verify(navUtilsWrapper).navigateUpFromSameTask();
    }
}
