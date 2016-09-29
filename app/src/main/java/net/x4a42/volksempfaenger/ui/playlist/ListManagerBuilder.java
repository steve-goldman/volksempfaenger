package net.x4a42.volksempfaenger.ui.playlist;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProviderBuilder;

class ListManagerBuilder
{
    public ListManager build(Context context)
    {
        ListViewManager listViewManager = new ListViewManagerBuilder().build(context);

        ListAdapterProxy listAdapterProxy
                = new ListAdapterProxyBuilder().build(context, listViewManager);

        ViewEpisodeActivityIntentProvider intentProvider
                = new ViewEpisodeActivityIntentProviderBuilder().build(context);

        PlaylistItemDaoWrapper playlistItemDao = new PlaylistItemDaoBuilder().build(context);

        return new ListManager(context,
                               listAdapterProxy,
                               listViewManager,
                               intentProvider,
                               playlistItemDao);
    }
}
