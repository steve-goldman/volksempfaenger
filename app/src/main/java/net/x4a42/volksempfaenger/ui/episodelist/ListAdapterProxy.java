package net.x4a42.volksempfaenger.ui.episodelist;

import android.view.View;
import android.view.ViewGroup;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;

class ListAdapterProxy
{
    private final ListAdapter       listAdapter;
    private final ListViewManager   listViewManager;
    private final EpisodeDaoWrapper episodeDao;
    private final Podcast           podcast;

    public ListAdapterProxy(ListAdapter       listAdapter,
                            ListViewManager   listViewManager,
                            EpisodeDaoWrapper episodeDao,
                            Podcast           podcast)
    {
        this.listAdapter     = listAdapter;
        this.listViewManager = listViewManager;
        this.episodeDao      = episodeDao;
        this.podcast         = podcast;
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
        listAdapter.addAll(episodeDao.getAll(podcast));
    }

    public void clear()
    {
        listAdapter.clear();
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ListViewHolder viewHolder = listViewManager.getViewHolder(convertView, parent);
        Episode        episode    = listAdapter.getItem(position);

        viewHolder.set(episode);

        return viewHolder.getView();
    }

    public long getItemId(int position)
    {
        return listAdapter.getItem(position).get_id();
    }
}
