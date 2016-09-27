package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.x4a42.volksempfaenger.R;

public class SubscriptionGridFragment extends Fragment
{
    private SubscriptionGridFragmentProxy proxy;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        proxy = new SubscriptionGridFragmentProxyBuilder().build(this);
        proxy.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         savedInstanceState)
    {
        return proxy.onCreateView(inflater, container);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        proxy.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        proxy.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        proxy.onDestroy();
        proxy = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        proxy.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return proxy.onOptionsItemSelected(item);
    }
}
