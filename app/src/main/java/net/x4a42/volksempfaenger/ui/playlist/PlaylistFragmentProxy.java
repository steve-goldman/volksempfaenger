package net.x4a42.volksempfaenger.ui.playlist;

import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

class PlaylistFragmentProxy
{
    private final Fragment    fragment;
    private final ListManager listManager;

    public PlaylistFragmentProxy(Fragment    fragment,
                                 ListManager listManager)
    {
        this.fragment    = fragment;
        this.listManager = listManager;
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
        listManager.refresh();
    }

    public void onPause()
    {
        listManager.clear();
    }
}
