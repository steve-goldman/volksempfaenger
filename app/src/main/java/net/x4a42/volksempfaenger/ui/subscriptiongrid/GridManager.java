package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.app.Activity;
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
import android.widget.GridView;
import android.widget.TextView;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDeleter;
import net.x4a42.volksempfaenger.ui.episodelist.EpisodeListActivityIntentProvider;

class GridManager implements AdapterView.OnItemClickListener,
                             AbsListView.MultiChoiceModeListener
{
    private final Activity                          activity;
    private final GridAdapterProxy                  gridAdapterProxy;
    private final EpisodeListActivityIntentProvider intentProvider;
    private final PodcastDaoWrapper                 podcastDao;
    private final PodcastDeleter                    podcastDeleter;
    private GridView                                gridView;
    private TextView                                noSubscriptionsView;

    public GridManager(Activity                          activity,
                       GridAdapterProxy                  gridAdapterProxy,
                       EpisodeListActivityIntentProvider intentProvider,
                       PodcastDaoWrapper                 podcastDao,
                       PodcastDeleter                    podcastDeleter)
    {
        this.activity         = activity;
        this.gridAdapterProxy = gridAdapterProxy;
        this.intentProvider   = intentProvider;
        this.podcastDao       = podcastDao;
        this.podcastDeleter   = podcastDeleter;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container)
    {
        View view = inflater.inflate(R.layout.subscription_list, container, false);

        gridView = (GridView) view.findViewById(R.id.grid);
        gridView.setOnItemClickListener(this);
        gridView.setAdapter(gridAdapterProxy.getAdapter());
        gridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(this);
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
        activity.startActivity(intent);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
    {
        if (gridView.getCheckedItemCount() == 0)
        {
            return;
        }

        Podcast podcast = podcastDao.getById(gridView.getCheckedItemIds()[0]);
        mode.setTitle(podcast.getTitle());
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu)
    {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.subscription_grid_context, menu);
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
            case R.id.subscription_grid_delete:
            {
                handleDelete();
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

    private void handleDelete()
    {
        Podcast podcast = podcastDao.getById(gridView.getCheckedItemIds()[0]);
        podcastDeleter.delete(podcast);
        gridAdapterProxy.refresh();
    }
}
