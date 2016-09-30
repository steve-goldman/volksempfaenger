package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;
import net.x4a42.volksempfaenger.ui.episodelist.EpisodeListActivityIntentProvider;

class GridManager implements AdapterView.OnItemClickListener
{
    private final Context                           context;
    private final GridAdapterProxy                  gridAdapterProxy;
    private final EpisodeListActivityIntentProvider intentProvider;
    private final PodcastDaoWrapper                 podcastDao;
    private GridView                                gridView;
    private TextView                                noSubscriptionsView;

    public GridManager(Context                           context,
                       GridAdapterProxy                  gridAdapterProxy,
                       EpisodeListActivityIntentProvider intentProvider,
                       PodcastDaoWrapper                 podcastDao)
    {
        this.context          = context;
        this.gridAdapterProxy = gridAdapterProxy;
        this.intentProvider   = intentProvider;
        this.podcastDao       = podcastDao;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container)
    {
        View view = inflater.inflate(R.layout.subscription_list, container, false);

        gridView = (GridView) view.findViewById(R.id.grid);
        gridView.setOnItemClickListener(this);
        gridView.setAdapter(gridAdapterProxy.getAdapter());

        noSubscriptionsView = (TextView) view.findViewById(R.id.empty);

        return view;
    }

    public void onResume()
    {
        gridAdapterProxy.refresh();
        if (gridAdapterProxy.isEmpty())
        {
            noSubscriptionsView.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        }
        else
        {
            noSubscriptionsView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        }
    }

    public void onPause()
    {
        gridAdapterProxy.clear();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Podcast podcast = podcastDao.getById(id);
        Intent  intent  = intentProvider.getIntent(podcast);
        context.startActivity(intent);
    }
}
