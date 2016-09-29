package net.x4a42.volksempfaenger.ui.episodelist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;

class ListAdapter extends ArrayAdapter<Episode>
{
    private ListAdapterProxy proxy;

    public ListAdapter(Context context)
    {
        // we override getView so the second parameter goes unused
        super(context, 0);
    }

    public ListAdapter setProxy(ListAdapterProxy proxy)
    {
        this.proxy = proxy;
        return this;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return proxy.getView(position, convertView, parent);
    }

    @Override
    public long getItemId(int position)
    {
        return proxy.getItemId(position);
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }
}
