package net.x4a42.volksempfaenger.ui.episodelist;

import android.content.Context;
import android.view.LayoutInflater;

class ListViewManagerBuilder
{
    public ListViewManager build(Context context)
    {
        ListViewHolderBuilder builder  = new ListViewHolderBuilder();
        LayoutInflater        inflater = LayoutInflater.from(context);
        return new ListViewManager(builder, inflater);
    }
}
