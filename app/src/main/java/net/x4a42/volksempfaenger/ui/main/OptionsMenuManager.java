package net.x4a42.volksempfaenger.ui.main;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.x4a42.volksempfaenger.R;

class OptionsMenuManager
{
    public interface Listener
    {
        void onAddSubscription();
        void onUpdateSubscriptions();
    }

    private Listener listener;

    public OptionsMenuManager setListener(Listener listener)
    {
        this.listener = listener;
        return this;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.subscription_list, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.item_add:
                listener.onAddSubscription();
                return true;
            case R.id.item_update:
                listener.onUpdateSubscriptions();
                return true;
        }
        return false;
    }
}
