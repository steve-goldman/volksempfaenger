package net.x4a42.volksempfaenger.ui.episodelist;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProvider;

class ListManager implements AdapterView.OnItemClickListener
{
    private final Context                           context;
    private final ListAdapterProxy                  listAdapterProxy;
    private final ListViewManager                   listViewManager;
    private final ViewEpisodeActivityIntentProvider intentProvider;
    private TextView                                noEpisodesView;

    public ListManager(Context                           context,
                       ListAdapterProxy                  listAdapterProxy,
                       ListViewManager                   listViewManager,
                       ViewEpisodeActivityIntentProvider intentProvider)
    {
        this.context          = context;
        this.listAdapterProxy = listAdapterProxy;
        this.listViewManager  = listViewManager;
        this.intentProvider   = intentProvider;
    }

    public void init(View view)
    {
        ListView listView = (ListView) view.findViewById(R.id.episode_list);
        listView.setOnItemClickListener(this);
        listView.setAdapter(listAdapterProxy.getAdapter());

        noEpisodesView    = (TextView) view.findViewById(R.id.empty);
    }

    public void refresh()
    {
        listAdapterProxy.refresh();
        noEpisodesView.setVisibility(listAdapterProxy.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }

    public void clear()
    {
        listAdapterProxy.clear();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Episode episode = listViewManager.getViewHolder(view).getEpisode();
        Intent  intent  = intentProvider.getIntent(episode);
        context.startActivity(intent);
    }
}
