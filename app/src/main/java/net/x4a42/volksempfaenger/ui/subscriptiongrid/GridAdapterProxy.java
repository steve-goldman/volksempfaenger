package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.view.View;
import android.view.ViewGroup;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;

import java.util.List;

class GridAdapterProxy
{
    private final GridAdapter       gridAdapter;
    private final GridViewManager   gridViewManager;
    private final PodcastDaoWrapper podcastDao;
    private List<Podcast>           list;

    public GridAdapterProxy(GridAdapter        gridAdapter,
                            GridViewManager    gridViewManager,
                            PodcastDaoWrapper  podcastDao)
    {
        this.gridAdapter     = gridAdapter;
        this.gridViewManager = gridViewManager;
        this.podcastDao      = podcastDao;
    }

    public void onResume()
    {
        list = podcastDao.getAll();
        gridAdapter.addAll(list);
    }

    public void onPause()
    {
        gridAdapter.clear();
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        GridViewHolder viewHolder = gridViewManager.getViewHolder(convertView, parent);
        Podcast        podcast    = list.get(position);

        viewHolder.set(podcast);

        return viewHolder.getView();
    }

}
