package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.content.Context;

class GridAdapterBuilder
{
    public GridAdapter build(Context context)
    {
        GridAdapter      gridAdapter = new GridAdapter(context);
        GridAdapterProxy proxy       = new GridAdapterProxyBuilder().build(gridAdapter);

        gridAdapter.setProxy(proxy);

        return gridAdapter;
    }
}
