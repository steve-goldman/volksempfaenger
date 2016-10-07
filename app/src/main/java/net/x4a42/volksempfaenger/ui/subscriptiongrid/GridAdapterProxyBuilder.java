package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;

class GridAdapterProxyBuilder
{
    public GridAdapterProxy build(Context         context)
    {
        GridViewManager gridViewManager
                = new GridViewManagerBuilder().build(context);

        GridAdapter       gridAdapter = new GridAdapter(context);
        PodcastDaoWrapper podcastDao  = new PodcastDaoBuilder().build(context);
        GridAdapterProxy  proxy       = new GridAdapterProxy(gridAdapter,
                                                             gridViewManager,
                                                             podcastDao);

        gridAdapter.setProxy(proxy);

        return proxy;
    }
}
