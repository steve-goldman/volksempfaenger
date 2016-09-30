package net.x4a42.volksempfaenger.ui.playlist;

import android.view.View;
import android.view.ViewGroup;

import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItem;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;

class ListAdapterProxy
{
    private final ListAdapter            listAdapter;
    private final ListViewManager        listViewManager;
    private final PlaylistItemDaoWrapper playlistItemDao;

    public ListAdapterProxy(ListAdapter            listAdapter,
                            ListViewManager        listViewManager,
                            PlaylistItemDaoWrapper playlistItemDao)
    {
        this.listAdapter     = listAdapter;
        this.listViewManager = listViewManager;
        this.playlistItemDao = playlistItemDao;
    }

    public ListAdapter getAdapter()
    {
        return listAdapter;
    }

    public boolean isEmpty()
    {
        return listAdapter.isEmpty();
    }

    public void refresh()
    {
        listAdapter.clear();
        listAdapter.addAll(playlistItemDao.getAll());
    }

    public void clear()
    {
        listAdapter.clear();
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ListViewHolder viewHolder   = listViewManager.getViewHolder(convertView, parent);
        PlaylistItem   playlistItem = listAdapter.getItem(position);

        viewHolder.set(playlistItem);

        return viewHolder.getView();
    }

    public long getItemId(int position)
    {
        return listAdapter.getItem(position).get_id();
    }

}
