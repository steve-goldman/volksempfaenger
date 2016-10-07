package net.x4a42.volksempfaenger.ui.playlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.x4a42.volksempfaenger.event.episodedownload.EpisodeDownloadEventReceiver;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventReceiver;

class PlaylistFragmentProxy
{
    private final ListManager                  listManager;
    private final PlaybackEventReceiver        playbackEventReceiver;
    private final EpisodeDownloadEventReceiver downloadEventReceiver;

    public PlaylistFragmentProxy(ListManager                  listManager,
                                 PlaybackEventReceiver        playbackEventReceiver,
                                 EpisodeDownloadEventReceiver downloadEventReceiver)
    {
        this.listManager           = listManager;
        this.playbackEventReceiver = playbackEventReceiver;
        this.downloadEventReceiver = downloadEventReceiver;
    }

    public void onCreate()
    {
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container)
    {
        return listManager.onCreateView(inflater, container);
    }

    public void onResume()
    {
        playbackEventReceiver.subscribe();
        downloadEventReceiver.subscribe();
        listManager.refresh();
    }

    public void onPause()
    {
        playbackEventReceiver.unsubscribe();
        downloadEventReceiver.unsubscribe();
        listManager.clear();
    }
}
