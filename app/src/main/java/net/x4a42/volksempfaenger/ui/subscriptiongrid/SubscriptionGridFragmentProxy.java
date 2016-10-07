package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

class SubscriptionGridFragmentProxy
{
    private final Fragment    fragment;
    private final GridManager gridManager;

    public SubscriptionGridFragmentProxy(Fragment    fragment,
                                         GridManager gridManager)
    {
        this.fragment    = fragment;
        this.gridManager = gridManager;
    }

    public void onCreate()
    {
        fragment.setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container)
    {
        return gridManager.onCreateView(inflater, container);
    }

    public void onResume()
    {
        gridManager.onResume();
    }

    public void onPause()
    {
        gridManager.onPause();
    }

    public void onDestroy()
    {

    }
}
