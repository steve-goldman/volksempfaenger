package net.x4a42.volksempfaenger.ui.playlist;

import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;

class PlaylistFragmentProxy
{
    private final ListManager           listManager;
    private final PlaybackEventReceiver receiver;

    public PlaylistFragmentProxy(ListManager           listManager,
                                 PlaybackEventReceiver receiver)
    {
        this.listManager = listManager;
        this.receiver    = receiver;
    }

    public void onCreate()
    {
        // TODO: set up options menu?
        //fragment.setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container)
    {
        return listManager.onCreateView(inflater, container);
    }

    public void onResume()
    {
        receiver.subscribe();
        listManager.refresh();
    }

    public void onPause()
    {
        receiver.unsubscribe();
        listManager.clear();
    }
}
