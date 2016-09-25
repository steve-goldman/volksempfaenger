package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.view.Menu;
import android.view.MenuInflater;

import net.x4a42.volksempfaenger.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class OptionsMenuManagerTest
{
    @Mock Menu         menu;
    @Mock MenuInflater inflater;
    OptionsMenuManager menuManager;

    @Before
    public void setUp() throws Exception
    {
        menuManager = new OptionsMenuManager();
    }

    @Test
    public void onCreateOptionsMenu() throws Exception
    {
        menuManager.onCreateOptionsMenu(menu, inflater);
        Mockito.verify(inflater).inflate(R.menu.subscription_list, menu);
    }


}