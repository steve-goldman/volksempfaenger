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

public class ViewEpisodeOptionsMenuManager
{
    public interface Listener
    {
        void onDownload();
        void onPlay();
        void onDelete();
        void onShare();
        void onMarkListened();
        void onMarkNew();
        void onWebsite();
        void onSettings();
        void onHome();
    }

    private final Uri                     episodeUri;
    private final MenuInflater            inflater;

    private Listener                      listener;
    private EpisodeCursorProvider cursorProvider;
    private PlaybackServiceFacadeProvider facadeProvider;

    public ViewEpisodeOptionsMenuManager(Uri          episodeUri,
                                         MenuInflater inflater)
    {
        this.episodeUri = episodeUri;
        this.inflater   = inflater;
    }

    public ViewEpisodeOptionsMenuManager setListener(Listener listener)
    {
        this.listener = listener;
        return this;
    }

    public ViewEpisodeOptionsMenuManager setCursorProvider(EpisodeCursorProvider cursorProvider)
    {
        this.cursorProvider = cursorProvider;
        return this;
    }

    public ViewEpisodeOptionsMenuManager setFacadeProvider(PlaybackServiceFacadeProvider facadeProvider)
    {
        this.facadeProvider = facadeProvider;
        return this;
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        inflater.inflate(R.menu.view_episode, menu);

        EpisodeCursor cursor = cursorProvider.getEpisodeCursor();

        menu.findItem(R.id.item_play).setVisible(cursor != null && canPlay());
        menu.findItem(R.id.item_download).setVisible(cursor != null && canDownload(cursor));
        menu.findItem(R.id.item_share).setVisible(cursor != null);
        menu.findItem(R.id.item_delete).setVisible(cursor != null && canDelete(cursor));
        menu.findItem(R.id.item_mark_listened).setVisible(cursor != null && canMarkListened(cursor));
        menu.findItem(R.id.item_mark_new).setVisible(cursor != null && canMarkNew(cursor));
        menu.findItem(R.id.item_website).setVisible(cursor != null);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.item_play:
                listener.onPlay();
                return true;
            case R.id.item_download:
                listener.onDownload();
                return true;
            case R.id.item_share:
                listener.onShare();
                return true;
            case R.id.item_delete:
                listener.onDelete();
                return true;
            case R.id.item_mark_listened:
                listener.onMarkListened();
                return true;
            case R.id.item_mark_new:
                listener.onMarkNew();
                return true;
            case R.id.item_website:
                listener.onWebsite();
                return true;
            case R.id.item_settings:
                listener.onSettings();
                return true;
            case android.R.id.home:
                listener.onHome();
                return true;
            default:
                return false;
        }
    }

    private boolean canPlay()
    {
        PlaybackServiceFacade facade = facadeProvider.getFacade();
        return !facade.isPlaying() || !episodeUri.equals(facade.getEpisodeUri());
    }

    private boolean canDownload(EpisodeCursor cursor)
    {
        int downloadStatus = cursor.getDownloadStatus();
        return downloadStatus == -1
                || downloadStatus == DownloadManager.STATUS_FAILED;
    }

    private boolean canDelete(EpisodeCursor cursor)
    {
        return !canDownload(cursor);
    }

    private boolean canMarkListened(EpisodeCursor cursor)
    {
        int status = cursor.getStatus();
        return status == Constants.EPISODE_STATE_NEW ||
                status == Constants.EPISODE_STATE_DOWNLOADING ||
                status == Constants.EPISODE_STATE_READY;
    }

    private boolean canMarkNew(EpisodeCursor cursor)
    {
        return !canMarkListened(cursor);
    }

}
