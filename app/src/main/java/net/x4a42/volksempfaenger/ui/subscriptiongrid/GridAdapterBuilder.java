package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.content.Context;

import net.x4a42.volksempfaenger.ui.subscriptiongrid.view.GridViewManager;

class GridAdapterBuilder
{
    public GridAdapter build(Context context, GridViewManager gridViewManager)
    {
        GridAdapter      gridAdapter = new GridAdapter(context);
        GridAdapterProxy proxy       = new GridAdapterProxyBuilder().build(gridAdapter, gridViewManager);

        gridAdapter.setProxy(proxy);

        return gridAdapter;
    }
}
