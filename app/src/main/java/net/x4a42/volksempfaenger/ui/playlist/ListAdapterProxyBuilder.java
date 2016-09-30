package net.x4a42.volksempfaenger.ui.playlist;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;

class ListAdapterProxyBuilder
{
    public ListAdapterProxy build(Context context)
    {
        ListViewManager        listViewManager = new ListViewManagerBuilder().build(context);
        ListAdapter            listAdapter     = new ListAdapter(context);
        PlaylistItemDaoWrapper playlistItemDao = new PlaylistItemDaoBuilder().build(context);
        ListAdapterProxy       proxy           = new ListAdapterProxy(listAdapter,
                                                                      listViewManager,
                                                                      playlistItemDao);

        listAdapter.setProxy(proxy);

        return proxy;
    }
}
