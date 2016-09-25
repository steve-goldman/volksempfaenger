package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import net.x4a42.volksempfaenger.R;

class GridManager implements AdapterView.OnItemClickListener
{
    private final GridAdapter gridAdapter;
    private TextView          noSubscriptionsView;
    private View              view;

    public GridManager(GridAdapter gridAdapter)
    {
        this.gridAdapter = gridAdapter;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container)
    {
        view = inflater.inflate(R.layout.subscription_list, container, false);

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

    }
}
