package net.x4a42.volksempfaenger.ui.viewepisode;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacadeProvider;

class OptionsMenuManager
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

    private final Episode                 episode;
    private final MenuInflater            inflater;

    private Listener                      listener;
    private PlaybackServiceFacadeProvider facadeProvider;

    public OptionsMenuManager(Episode      episode,
                              MenuInflater inflater)
    {
        this.episode    = episode;
        this.inflater   = inflater;
    }

    public OptionsMenuManager setListener(Listener listener)
    {
        this.listener = listener;
        return this;
    }

    public OptionsMenuManager setFacadeProvider(PlaybackServiceFacadeProvider facadeProvider)
    {
        this.facadeProvider = facadeProvider;
        return this;
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        inflater.inflate(R.menu.view_episode, menu);

        menu.findItem(R.id.item_play).setVisible(canPlay());
        menu.findItem(R.id.item_download).setVisible(canDownload());
        menu.findItem(R.id.item_delete).setVisible(canDelete());
        menu.findItem(R.id.item_mark_listened).setVisible(canMarkListened());
        menu.findItem(R.id.item_mark_new).setVisible(canMarkNew());

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
        return facade != null &&
                (!facade.isPlaying() || !facade.isEpisodeOpen(episode));
    }

    private boolean canDownload()
    {
        // TODO
        return true;
    }

    private boolean canDelete()
    {
        return !canDownload();
    }

    private boolean canMarkListened()
    {
        // TODO
        return true;
    }

    private boolean canMarkNew()
    {
        return !canMarkListened();
    }

}
