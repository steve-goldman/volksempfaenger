package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.DownloadManager;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.episode.EpisodeCursorProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacadeProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OptionsMenuManagerTest
{
    @Mock Uri                           episodeUri;
    @Mock Uri                           otherEpisodeUri;
    @Mock EpisodeCursorProvider         cursorProvider;
    @Mock EpisodeCursor                 cursor;
    @Mock PlaybackServiceFacadeProvider facadeProvider;
    @Mock PlaybackServiceFacade         facade;
    @Mock MenuInflater                  inflater;
    @Mock Menu                          menu;
    @Mock MenuItem                      downloadItem;
    @Mock MenuItem                      playItem;
    @Mock MenuItem                      shareItem;
    @Mock MenuItem                      deleteItem;
    @Mock MenuItem                      markListenedItem;
    @Mock MenuItem                      markNewItem;
    @Mock MenuItem                      websiteItem;
    @Mock OptionsMenuManager.Listener   listener;

    OptionsMenuManager                  optionsMenuManager;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(cursorProvider.getEpisodeCursor()).thenReturn(cursor);
        Mockito.when(facadeProvider.getFacade()).thenReturn(facade);
        Mockito.when(menu.findItem(R.id.item_play)).thenReturn(playItem);
        Mockito.when(menu.findItem(R.id.item_download)).thenReturn(downloadItem);
        Mockito.when(menu.findItem(R.id.item_share)).thenReturn(shareItem);
        Mockito.when(menu.findItem(R.id.item_delete)).thenReturn(deleteItem);
        Mockito.when(menu.findItem(R.id.item_mark_listened)).thenReturn(markListenedItem);
        Mockito.when(menu.findItem(R.id.item_mark_new)).thenReturn(markNewItem);
        Mockito.when(menu.findItem(R.id.item_website)).thenReturn(websiteItem);

        optionsMenuManager
                = new OptionsMenuManager(episodeUri, inflater)
                .setCursorProvider(cursorProvider)
                .setFacadeProvider(facadeProvider)
                .setListener(listener);
    }

    @Test
    public void onCreateNoCursor() throws Exception
    {
        Mockito.when(cursorProvider.getEpisodeCursor()).thenReturn(null);

        optionsMenuManager.onCreateOptionsMenu(menu);

        Mockito.verify(playItem).setVisible(false);
        Mockito.verify(downloadItem).setVisible(false);
        Mockito.verify(shareItem).setVisible(false);
        Mockito.verify(deleteItem).setVisible(false);
        Mockito.verify(markListenedItem).setVisible(false);
        Mockito.verify(markNewItem).setVisible(false);
        Mockito.verify(websiteItem).setVisible(false);
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
        Mockito.when(facade.getEpisodeUri()).thenReturn(otherEpisodeUri);

        optionsMenuManager.onCreateOptionsMenu(menu);

        Mockito.verify(playItem).setVisible(true);
    }

    @Test
    public void onCreatePlayingSameEpisode() throws Exception
    {
        Mockito.when(facade.isPlaying()).thenReturn(true);
        Mockito.when(facade.getEpisodeUri()).thenReturn(episodeUri);

        optionsMenuManager.onCreateOptionsMenu(menu);

        Mockito.verify(playItem).setVisible(false);
    }

    @Test
    public void onCreateNotDownloaded() throws Exception
    {
        Mockito.when(cursor.getDownloadStatus()).thenReturn(-1);

        optionsMenuManager.onCreateOptionsMenu(menu);

        Mockito.verify(downloadItem).setVisible(true);
    }

    @Test
    public void onCreateCanDownload() throws Exception
    {
        for (int status : new int[] { -1, DownloadManager.STATUS_FAILED } )
        {
            Mockito.reset(downloadItem, deleteItem);

            Mockito.when(cursor.getDownloadStatus()).thenReturn(status);

            optionsMenuManager.onCreateOptionsMenu(menu);

            Mockito.verify(downloadItem).setVisible(true);
            Mockito.verify(deleteItem).setVisible(false);
        }
    }

    @Test
    public void onCreateCannotDownload() throws Exception
    {
        for (int status : new int[]
                {
                    DownloadManager.STATUS_PAUSED,
                    DownloadManager.STATUS_PENDING,
                    DownloadManager.STATUS_RUNNING,
                    DownloadManager.STATUS_SUCCESSFUL
                })
        {
            Mockito.reset(downloadItem, deleteItem);

            Mockito.when(cursor.getDownloadStatus()).thenReturn(status);

            optionsMenuManager.onCreateOptionsMenu(menu);

            Mockito.verify(downloadItem).setVisible(false);
            Mockito.verify(deleteItem).setVisible(true);
        }
    }

    @Test
    public void onCreateCanMarkListened() throws Exception
    {
        for (int status : new int[]
                {
                        Constants.EPISODE_STATE_NEW,
                        Constants.EPISODE_STATE_DOWNLOADING,
                        Constants.EPISODE_STATE_READY
                })
        {
            Mockito.reset(markListenedItem, markNewItem);

            Mockito.when(cursor.getStatus()).thenReturn(status);

            optionsMenuManager.onCreateOptionsMenu(menu);

            Mockito.verify(markListenedItem).setVisible(true);
            Mockito.verify(markNewItem).setVisible(false);
        }
    }

    @Test
    public void onCreateCanMarkNew() throws Exception
    {
        for (int status : new int[]
                {
                        Constants.EPISODE_STATE_LISTENING,
                        Constants.EPISODE_STATE_LISTENED
                })
        {
            Mockito.reset(markListenedItem, markNewItem);

            Mockito.when(cursor.getStatus()).thenReturn(status);

            optionsMenuManager.onCreateOptionsMenu(menu);

            Mockito.verify(markListenedItem).setVisible(false);
            Mockito.verify(markNewItem).setVisible(true);
        }
    }

}
