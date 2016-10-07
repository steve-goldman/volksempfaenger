package net.x4a42.volksempfaenger.ui.episodelist;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;

class ListAdapterProxyBuilder
{
    public ListAdapterProxy build(Context         context,
                                  Podcast         podcast)
    {
        ListViewManager   listViewManager = new ListViewManagerBuilder().build(context);
        ListAdapter       listAdapter     = new ListAdapter(context);
        EpisodeDaoWrapper episodeDao      = new EpisodeDaoBuilder().build(context);
        ListAdapterProxy  proxy           = new ListAdapterProxy(listAdapter,
                                                                 listViewManager,
                                                                 episodeDao,
                                                                 podcast);

        listAdapter.setProxy(proxy);

        return proxy;
    }
}
