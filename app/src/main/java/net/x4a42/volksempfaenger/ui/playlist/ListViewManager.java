package net.x4a42.volksempfaenger.ui.playlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.x4a42.volksempfaenger.R;

class ListViewManager
{
    private final ListViewHolderBuilder builder;
    private final LayoutInflater        inflater;

    public ListViewManager(ListViewHolderBuilder builder,
                           LayoutInflater        inflater)
    {
        this.builder  = builder;
        this.inflater = inflater;
    }

    public ListViewHolder getViewHolder(View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = createView(parent);
            convertView.setTag(builder.build(convertView));
        }

        return getViewHolder(convertView);
    }

    public ListViewHolder getViewHolder(View view)
    {
        return (ListViewHolder) view.getTag();
    }

    private View createView(ViewGroup parent)
    {
        return inflater.inflate(R.layout.episode_list_row, parent, false);
    }
}
