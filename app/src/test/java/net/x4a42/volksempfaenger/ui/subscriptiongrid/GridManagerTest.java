package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.ui.episodelist.view.EpisodeListActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.subscriptiongrid.view.GridViewManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GridManagerTest
{
    @Mock Context                           context;
    @Mock GridAdapter                       gridAdapter;
    @Mock LayoutInflater                    inflater;
    @Mock ViewGroup                         container;
    @Mock View                              view;
    @Mock GridView                          gridView;
    @Mock TextView                          noSubscriptionsView;
    @Mock GridViewManager                   gridViewManager;
    @Mock EpisodeListActivityIntentProvider intentProvider;
    GridManager                             gridManager;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(inflater.inflate(R.layout.subscription_list, container, false))
               .thenReturn(view);
        Mockito.when(view.findViewById(R.id.grid)).thenReturn(gridView);
        Mockito.when(view.findViewById(R.id.empty)).thenReturn(noSubscriptionsView);
        gridManager = new GridManager(context, gridAdapter, gridViewManager, intentProvider);
    }

    @Test
    public void onCreateView() throws Exception
    {
        View result = gridManager.onCreateView(inflater, container);
        assertEquals(view, result);
        Mockito.verify(gridView).setOnItemClickListener(gridManager);
        Mockito.verify(gridView).setAdapter(gridAdapter);
    }

    @Test
    public void onResume() throws Exception
    {
        gridManager.onCreateView(inflater, container);
        gridManager.onResume();

        Mockito.verify(gridAdapter).onResume();
        Mockito.verify(noSubscriptionsView).setVisibility(View.INVISIBLE);
    }

    @Test
    public void onResumeEmpty() throws Exception
    {
        Mockito.when(gridAdapter.isEmpty()).thenReturn(true);

        gridManager.onCreateView(inflater, container);
        gridManager.onResume();

        Mockito.verify(gridAdapter).onResume();
        Mockito.verify(noSubscriptionsView).setVisibility(View.VISIBLE);
    }

    @Test
    public void onPause() throws Exception
    {
        gridManager.onPause();
        Mockito.verify(gridAdapter).onPause();
    }

    @Test
    public void onItemClick() throws Exception
    {

    }
}