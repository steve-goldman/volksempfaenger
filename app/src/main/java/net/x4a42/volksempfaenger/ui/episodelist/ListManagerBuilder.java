package net.x4a42.volksempfaenger.ui.episodelist;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.ui.episodelist.view.ListViewManager;
import net.x4a42.volksempfaenger.ui.episodelist.view.ListViewManagerBuilder;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProviderBuilder;

class ListManagerBuilder
{
    public ListManager build(Context context, Podcast podcast)
    {
        ListViewManager  listViewManager = new ListViewManagerBuilder().build(context);

        ListAdapterProxy listAdapterProxy
                = new ListAdapterProxyBuilder().build(context, listViewManager, podcast);

        ViewEpisodeActivityIntentProvider intentProvider
                = new ViewEpisodeActivityIntentProviderBuilder().build(context);

        return new ListManager(context,
                               listAdapterProxy,
                               listViewManager,
                               intentProvider);
    }
}
