package net.x4a42.volksempfaenger.ui.subscriptiongrid.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.x4a42.volksempfaenger.R;

public class GridViewManager
{
    private final GridViewHolderBuilder builder;
    private final LayoutInflater        inflater;

    public GridViewManager(GridViewHolderBuilder builder,
                           LayoutInflater        inflater)
    {
        this.builder  = builder;
        this.inflater = inflater;
    }

    public GridViewHolder getViewHolder(View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            View           view       = createView(parent);
            GridViewHolder viewHolder = builder.build(view);
            view.setTag(viewHolder);
            return viewHolder;
        }

        return (GridViewHolder) convertView.getTag();
    }

    private View createView(ViewGroup parent)
    {
        return inflater.inflate(R.layout.subscription_list_row, parent, false);
    }
}
