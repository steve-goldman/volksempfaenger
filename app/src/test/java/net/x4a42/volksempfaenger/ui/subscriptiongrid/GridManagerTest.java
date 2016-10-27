package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDeleter;
import net.x4a42.volksempfaenger.ui.episodelist.EpisodeListActivityIntentProvider;

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
    @Mock Activity                          activity;
    @Mock GridAdapterProxy                  gridAdapterProxy;
    @Mock GridAdapter                       gridAdapter;
    @Mock LayoutInflater                    inflater;
    @Mock ViewGroup                         container;
    @Mock View                              view;
    @Mock GridView                          gridView;
    @Mock TextView                          noSubscriptionsView;
    @Mock PodcastDaoWrapper                 podcastDaoWrapper;
    @Mock PodcastDeleter                    podcastDeleter;
    @Mock EpisodeListActivityIntentProvider intentProvider;
    GridManager                             gridManager;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(inflater.inflate(R.layout.subscription_list, container, false))
               .thenReturn(view);
        Mockito.when(view.findViewById(R.id.grid)).thenReturn(gridView);
        Mockito.when(view.findViewById(R.id.empty)).thenReturn(noSubscriptionsView);
        Mockito.when(gridAdapterProxy.getAdapter()).thenReturn(gridAdapter);
        gridManager = new GridManager(activity,
                                      gridAdapterProxy,
                                      intentProvider,
                                      podcastDaoWrapper,
                                      podcastDeleter);
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

        Mockito.verify(gridAdapterProxy).refresh();
        Mockito.verify(noSubscriptionsView).setVisibility(View.GONE);
        Mockito.verify(gridView).setVisibility(View.VISIBLE);
    }

    @Test
    public void onResumeEmpty() throws Exception
    {
        Mockito.when(gridAdapterProxy.isEmpty()).thenReturn(true);

        gridManager.onCreateView(inflater, container);
        gridManager.onResume();

        Mockito.verify(gridAdapterProxy).refresh();
        Mockito.verify(noSubscriptionsView).setVisibility(View.VISIBLE);
        Mockito.verify(gridView).setVisibility(View.GONE);
    }

    @Test
    public void onPause() throws Exception
    {
        gridManager.onPause();
        Mockito.verify(gridAdapterProxy).clear();
    }

    @Test
    public void onItemClick() throws Exception
    {

    }
}