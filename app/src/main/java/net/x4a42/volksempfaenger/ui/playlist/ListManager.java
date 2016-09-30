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
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItem;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProvider;

class ListManager implements AdapterView.OnItemClickListener,
                             AbsListView.MultiChoiceModeListener,
                             DragSortListView.DropListener,
                             DragSortListView.RemoveListener
{
    private final Context                           context;
    private final ListAdapterProxy                  listAdapterProxy;
    private final ViewEpisodeActivityIntentProvider intentProvider;
    private final PlaylistItemDaoWrapper            playlistItemDao;
    private DragSortListView                        listView;
    private TextView                                noEpisodesView;

    public ListManager(Context                           context,
                       ListAdapterProxy                  listAdapterProxy,
                       ViewEpisodeActivityIntentProvider intentProvider,
                       PlaylistItemDaoWrapper            playlistItemDao)
    {
        this.context          = context;
        this.listAdapterProxy = listAdapterProxy;
        this.intentProvider   = intentProvider;
        this.playlistItemDao  = playlistItemDao;
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
        PlaylistItem playlistItem = playlistItemDao.getById(id);
        Intent       intent       = intentProvider.getIntent(playlistItem.getEpisode());
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
                handleRemove();
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

    //
    // DragSortListView.DragListener
    //

    @Override
    public void drop(int fromPosition, int toPosition)
    {
        if (fromPosition == toPosition)
        {
            return;
        }

        PlaylistItem playlistItem = playlistItemDao.getByPosition(fromPosition);
        playlistItemDao.move(playlistItem, toPosition);
        listAdapterProxy.refresh();
    }

    //
    // DragSortListView.RemoveListener
    //

    @Override
    public void remove(int position)
    {
        PlaylistItem playlistItem = playlistItemDao.getByPosition(position);
        playlistItemDao.delete(playlistItem);
        listAdapterProxy.refresh();
    }

    private void handleRemove()
    {
        for (long playlistItemId : listView.getCheckedItemIds())
        {
            PlaylistItem playlistItem = playlistItemDao.getById(playlistItemId);
            playlistItemDao.delete(playlistItem);
        }
        listAdapterProxy.refresh();
    }
}
