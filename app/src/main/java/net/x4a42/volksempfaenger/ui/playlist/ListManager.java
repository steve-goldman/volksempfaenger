package net.x4a42.volksempfaenger.ui.playlist;

import android.content.Context;
import android.content.Intent;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.ToastMaker;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.playlist.Playlist;
import net.x4a42.volksempfaenger.event.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventListener;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProvider;

class ListManager implements AdapterView.OnItemClickListener,
                             AbsListView.MultiChoiceModeListener,
                             DragSortListView.DropListener,
                             DragSortListView.RemoveListener,
                             PlaybackEventListener
{
    private final Context                           context;
    private final ListAdapterProxy                  listAdapterProxy;
    private final ViewEpisodeActivityIntentProvider intentProvider;
    private final Playlist                          playlist;
    private final ToastMaker                        toastMaker;
    private DragSortListView                        listView;
    private TextView                                noEpisodesView;

    public ListManager(Context                           context,
                       ListAdapterProxy                  listAdapterProxy,
                       ViewEpisodeActivityIntentProvider intentProvider,
                       Playlist                          playlist,
                       ToastMaker                        toastMaker)
    {
        this.context          = context;
        this.listAdapterProxy = listAdapterProxy;
        this.intentProvider   = intentProvider;
        this.playlist         = playlist;
        this.toastMaker       = toastMaker;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container)
    {
        View view = inflater.inflate(R.layout.playlist, container, false);

        listView = (DragSortListView) view.findViewById(R.id.playlist_list);
        listView.setDropListener(this);
        listView.setRemoveListener(this);
        listView.setOnItemClickListener(this);
        listView.setAdapter(listAdapterProxy.getAdapter());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(this);

        noEpisodesView = (TextView) view.findViewById(R.id.playlist_empty);

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

    //
    // AdapterView.OnItemClickListener
    //

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Episode episode = playlist.getEpisode(position);
        Intent  intent  = intentProvider.getIntent(episode);
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
                                R.string.playlist_context_title), listView.getCheckedItemCount()));
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu)
    {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.playlist_context, menu);
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
            case R.id.item_playlist_remove:
            {
                if (!playlist.removeItem(listView.getCheckedItemIds()))
                {
                    toastMaker.showTextShort(context.getString(R.string.toast_cannot_change_playing_episode));
                }
                mode.finish();
                listAdapterProxy.refresh();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode)
    {
    }

    //
    // DragSortListView.DragListener
    //

    @Override
    public void drop(int fromPosition, int toPosition)
    {
        if (!playlist.moveItem(fromPosition, toPosition))
        {
            toastMaker.showTextShort(context.getString(R.string.toast_cannot_change_playing_episode));
            return;
        }

        listAdapterProxy.refresh();
    }

    //
    // DragSortListView.RemoveListener
    //

    @Override
    public void remove(int position)
    {
        if (!playlist.removeItem(position))
        {
            toastMaker.showTextShort(context.getString(R.string.toast_cannot_change_playing_episode));
            return;
        }

        listAdapterProxy.refresh();
    }

    //
    // PlaybackEventListener
    //

    @Override
    public void onPlaybackEvent(PlaybackEvent playbackEvent)
    {
        listAdapterProxy.refresh();
    }
}
