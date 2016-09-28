package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.view.View;
import android.view.ViewGroup;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GridAdapterProxyTest
{
    @Mock GridAdapter       gridAdapter;
    @Mock
    GridViewManager gridViewManager;
    @Mock View              convertView;
    @Mock ViewGroup         parent;
    @Mock
    GridViewHolder viewHolder;
    @Mock PodcastDaoWrapper podcastDao;
    @Mock Podcast           podcast;
    @Mock List<Podcast>     list;
    GridAdapterProxy        proxy;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(podcastDao.getAll()).thenReturn(list);
        Mockito.when(gridViewManager.getViewHolder(convertView, parent)).thenReturn(viewHolder);
        proxy = new GridAdapterProxy(gridAdapter, gridViewManager, podcastDao);
    }

    @Test
    public void onResume() throws Exception
    {
        proxy.onResume();
        Mockito.verify(gridAdapter).addAll(list);
    }

    @Test
    public void onPause() throws Exception
    {
        proxy.onPause();
        Mockito.verify(gridAdapter).clear();
    }

    @Test
    public void getView() throws Exception
    {

    }
}