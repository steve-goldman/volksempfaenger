package net.x4a42.volksempfaenger.ui.episodelist;

import android.content.Context;
import android.content.Intent;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.ToastMaker;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;
import net.x4a42.volksempfaenger.data.playlist.Playlist;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProvider;

class ListManager implements AdapterView.OnItemClickListener,
                             AbsListView.MultiChoiceModeListener

{
    private final Context                           context;
    private final ListAdapterProxy                  listAdapterProxy;
    private final ViewEpisodeActivityIntentProvider viewEpisodeIntentProvider;
    private final EpisodeDaoWrapper                 episodeDao;
    private final Playlist                          playlist;
    private final ToastMaker                        toastMaker;
    private ListView                                listView;
    private TextView                                noEpisodesView;

    public ListManager(Context                           context,
                       ListAdapterProxy                  listAdapterProxy,
                       ViewEpisodeActivityIntentProvider viewEpisodeIntentProvider,
                       EpisodeDaoWrapper                 episodeDao,
                       Playlist                          playlist,
                       ToastMaker                        toastMaker)
    {
        this.context                    = context;
        this.listAdapterProxy           = listAdapterProxy;
        this.viewEpisodeIntentProvider  = viewEpisodeIntentProvider;
        this.episodeDao                 = episodeDao;
        this.playlist                   = playlist;
        this.toastMaker                 = toastMaker;
    }

    public void init(View view)
    {
        listView = (ListView) view.findViewById(R.id.episode_list);
        listView.setOnItemClickListener(this);
        listView.setAdapter(listAdapterProxy.getAdapter());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(this);

        noEpisodesView    = (TextView) view.findViewById(R.id.empty);
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

    //
    // AdapterView.OnItemClickListener
    //

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Episode episode = episodeDao.getById(id);
        Intent  intent  = viewEpisodeIntentProvider.getIntent(episode);
        context.startActivity(intent);
    }

    //
    // AbsListView.MultiChoiceModeListener
    //

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
    {
        mode.setTitle(
                String.format(
                        context.getResources().getString(
                                R.string.episode_list_context_title),
                        listView.getCheckedItemCount()));
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu)
    {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.episode_list_context, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu)
    {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.item_episode_list_play_next:
            {
                handlePlay();
                mode.finish();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode)
    {
    }

    private void handlePlay()
    {
        if (!playlist.playEpisodesNow(listView.getCheckedItemIds()))
        {
            toastMaker.showTextShort(context.getString(R.string.toast_episode_enqueued));
        }
    }
}
