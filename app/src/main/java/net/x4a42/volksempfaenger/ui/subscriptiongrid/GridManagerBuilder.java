package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.content.Context;

class GridManagerBuilder
{
    public GridManager build(Context context)
    {
        GridAdapter gridAdapter
                = new GridAdapterBuilder().build(context);

        return new GridManager(gridAdapter);
    }
}
