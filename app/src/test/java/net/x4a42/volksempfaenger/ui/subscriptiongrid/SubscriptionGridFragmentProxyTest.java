package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionGridFragmentProxyTest
{
    @Mock Fragment                              fragment;
    @Mock GridManager                           gridManager;
    @Mock LayoutInflater                        inflater;
    @Mock ViewGroup                             container;
    @Mock Menu                                  menu;
    @Mock MenuInflater                          menuInflater;
    @Mock MenuItem                              menuItem;
    SubscriptionGridFragmentProxy               proxy;

    @Before
    public void setUp() throws Exception
    {
        proxy = new SubscriptionGridFragmentProxy(fragment,
                                                  gridManager);
    }

    @Test
    public void onCreate() throws Exception
    {
        proxy.onCreate();
        Mockito.verify(fragment).setHasOptionsMenu(true);
    }

    @Test
    public void onCreateView() throws Exception
    {
        proxy.onCreateView(inflater, container);
        Mockito.verify(gridManager).onCreateView(inflater, container);
    }

    @Test
    public void onResume() throws Exception
    {
        proxy.onResume();
        Mockito.verify(gridManager).onResume();
    }

    @Test
    public void onPause() throws Exception
    {
        proxy.onPause();
        Mockito.verify(gridManager).onPause();
    }

    @Test
    public void onDestroy() throws Exception
    {
        proxy.onDestroy();
    }
}