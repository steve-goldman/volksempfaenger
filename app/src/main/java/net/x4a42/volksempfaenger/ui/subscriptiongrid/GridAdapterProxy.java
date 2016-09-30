package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.view.View;
import android.view.ViewGroup;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;

class GridAdapterProxy
{
    private final GridAdapter       gridAdapter;
    private final GridViewManager   gridViewManager;
    private final PodcastDaoWrapper podcastDao;

    public GridAdapterProxy(GridAdapter        gridAdapter,
                            GridViewManager    gridViewManager,
                            PodcastDaoWrapper  podcastDao)
    {
        this.gridAdapter     = gridAdapter;
        this.gridViewManager = gridViewManager;
        this.podcastDao      = podcastDao;
    }

    public void refresh()
    {
        gridAdapter.clear();
        gridAdapter.addAll(podcastDao.getAll());
    }

    public void clear()
    {
        gridAdapter.clear();
    }

    public boolean isEmpty()
    {
        return gridAdapter.isEmpty();
    }

    public GridAdapter getAdapter()
    {
        return gridAdapter;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        GridViewHolder viewHolder = gridViewManager.getViewHolder(convertView, parent);
        Podcast        podcast    = gridAdapter.getItem(position);

        viewHolder.set(podcast);

        return viewHolder.getView();
    }

    public long getItemId(int position)
    {
        return gridAdapter.getItem(position).get_id();
    }

}
