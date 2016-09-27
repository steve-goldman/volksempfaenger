package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.x4a42.volksempfaenger.ui.addsubscription.AddSubscriptionActivityIntentProvider;

class SubscriptionGridFragmentProxy implements OptionsMenuManager.Listener
{
    private final Fragment                              fragment;
    private final GridManager                           gridManager;
    private final OptionsMenuManager                    menuManager;
    private final AddSubscriptionActivityIntentProvider addSubscriptionIntentProvider;

    public SubscriptionGridFragmentProxy(Fragment                              fragment,
                                         GridManager                           gridManager,
                                         OptionsMenuManager                    menuManager,
                                         AddSubscriptionActivityIntentProvider addSubscriptionIntentProvider)
    {
        this.fragment                      = fragment;
        this.gridManager                   = gridManager;
        this.menuManager                   = menuManager;
        this.addSubscriptionIntentProvider = addSubscriptionIntentProvider;
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

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menuManager.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        return menuManager.onOptionsItemSelected(item);
    }

    //
    // OptionsMenuManager.Listener
    //

    @Override
    public void onAddSubscription()
    {
        fragment.startActivity(addSubscriptionIntentProvider.get());
    }

    @Override
    public void onUpdateSubscriptions()
    {

    }
}
