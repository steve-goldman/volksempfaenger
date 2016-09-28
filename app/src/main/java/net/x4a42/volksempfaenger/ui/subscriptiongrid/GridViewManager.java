package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.x4a42.volksempfaenger.R;

class GridViewManager
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
            convertView = createView(parent);
            convertView.setTag(builder.build(convertView));
        }

        return getViewHolder(convertView);
    }

    public GridViewHolder getViewHolder(View view)
    {
        return (GridViewHolder) view.getTag();
    }

    private View createView(ViewGroup parent)
    {
        return inflater.inflate(R.layout.subscription_list_row, parent, false);
    }
}
