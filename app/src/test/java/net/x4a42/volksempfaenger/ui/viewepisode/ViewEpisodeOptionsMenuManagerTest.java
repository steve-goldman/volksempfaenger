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
import org.mockito.Mockito;

public class ViewEpisodeOptionsMenuManagerTest
{
    Uri                           episodeUri            = Mockito.mock(Uri.class);
    Uri                           otherEpisodeUri       = Mockito.mock(Uri.class);
    EpisodeCursorProvider         cursorProvider        = Mockito.mock(EpisodeCursorProvider.class);
    EpisodeCursor                 cursor                = Mockito.mock(EpisodeCursor.class);
    PlaybackServiceFacadeProvider facadeProvider        = Mockito.mock(PlaybackServiceFacadeProvider.class);
    PlaybackServiceFacade         facade                = Mockito.mock(PlaybackServiceFacade.class);
    MenuInflater                  inflater              = Mockito.mock(MenuInflater.class);
    Menu                          menu                  = Mockito.mock(Menu.class);
    MenuItem                      downloadItem          = Mockito.mock(MenuItem.class);
    MenuItem                      playItem              = Mockito.mock(MenuItem.class);
    MenuItem                      shareItem             = Mockito.mock(MenuItem.class);
    MenuItem                      deleteItem            = Mockito.mock(MenuItem.class);
    MenuItem                      markListenedItem      = Mockito.mock(MenuItem.class);
    MenuItem                      markNewItem           = Mockito.mock(MenuItem.class);
    MenuItem                      websiteItem           = Mockito.mock(MenuItem.class);
    ViewEpisodeOptionsMenuManager.Listener listener
            = Mockito.mock(ViewEpisodeOptionsMenuManager.Listener.class);

    ViewEpisodeOptionsMenuManager optionsMenuManager
            = new ViewEpisodeOptionsMenuManager(episodeUri,
                                                inflater)
            .setCursorProvider(cursorProvider)
            .setFacadeProvider(facadeProvider)
            .setListener(listener);

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
