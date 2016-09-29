package net.x4a42.volksempfaenger.ui.playlist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItem;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProvider;

class ListManager implements AdapterView.OnItemClickListener
{
    private final Context                           context;
    private final ListAdapterProxy                  listAdapterProxy;
    private final ListViewManager                   listViewManager;
    private final ViewEpisodeActivityIntentProvider intentProvider;
    private ListView                                listView;
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container)
    {
        View view = inflater.inflate(R.layout.episode_list_default, container, false);

        listView = (ListView) view.findViewById(R.id.playlist_list);
        listView.setOnItemClickListener(this);
        listView.setAdapter(listAdapterProxy.getAdapter());

        noEpisodesView = (TextView) view.findViewById(R.id.empty);

        return view;
    }

    public void refresh()
    {
        listAdapterProxy.refresh();
        if (listAdapterProxy.isEmpty())
        {
            noEpisodesView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        else
        {
            noEpisodesView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    public void clear()
    {
        listAdapterProxy.clear();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        PlaylistItem playlistItem = listViewManager.getViewHolder(view).getPlaylistItem();
        Intent       intent       = intentProvider.getIntent(playlistItem.getEpisode());
        context.startActivity(intent);
    }
}
