package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.content.Context;
import android.view.LayoutInflater;

class GridViewManagerBuilder
{
    public GridViewManager build(Context context)
    {
        GridViewHolderBuilder builder  = new GridViewHolderBuilder();
        LayoutInflater        inflater = LayoutInflater.from(context);
        return new GridViewManager(builder, inflater);
    }
}
