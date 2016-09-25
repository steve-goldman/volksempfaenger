package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;
import net.x4a42.volksempfaenger.ui.subscriptiongrid.view.GridViewManager;
import net.x4a42.volksempfaenger.ui.subscriptiongrid.view.GridViewManagerBuilder;

class GridAdapterProxyBuilder
{
    public GridAdapterProxy build(GridAdapter gridAdapter)
    {
        Context context = gridAdapter.getContext();

        GridViewManager gridViewManager
                = new GridViewManagerBuilder().build(context);

        PodcastDaoWrapper podcastDao
                = new PodcastDaoBuilder().build(context);

        return new GridAdapterProxy(gridAdapter, gridViewManager, podcastDao);
    }
}
