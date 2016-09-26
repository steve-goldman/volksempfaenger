package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.ui.subscriptiongrid.view.GridViewManager;

class GridManager implements AdapterView.OnItemClickListener
{
    private final GridAdapter     gridAdapter;
    private final GridViewManager gridViewManager;
    private TextView              noSubscriptionsView;

    public GridManager(GridAdapter     gridAdapter,
                       GridViewManager gridViewManager)
    {
        this.gridAdapter     = gridAdapter;
        this.gridViewManager = gridViewManager;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container)
    {
        View view = inflater.inflate(R.layout.subscription_list, container, false);

        GridView gridView = (GridView) view.findViewById(R.id.grid);
        gridView.setOnItemClickListener(this);
        gridView.setAdapter(gridAdapter);

        noSubscriptionsView = (TextView) view.findViewById(R.id.empty);

        return view;
    }

    public void onResume()
    {
        gridAdapter.onResume();
        noSubscriptionsView.setVisibility(gridAdapter.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }

    public void onPause()
    {
        gridAdapter.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Podcast podcast = gridViewManager.getViewHolder(view).getPodcast();
        // TODO: start episode list activity
    }
}
