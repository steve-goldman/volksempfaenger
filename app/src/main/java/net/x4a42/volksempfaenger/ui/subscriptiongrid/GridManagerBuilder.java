package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.content.Context;

import net.x4a42.volksempfaenger.ui.subscriptiongrid.view.GridViewManager;
import net.x4a42.volksempfaenger.ui.subscriptiongrid.view.GridViewManagerBuilder;

class GridManagerBuilder
{
    public GridManager build(Context context)
    {
        GridViewManager gridViewManager
                = new GridViewManagerBuilder().build(context);

        GridAdapter gridAdapter
                = new GridAdapterBuilder().build(context, gridViewManager);

        return new GridManager(gridAdapter, gridViewManager);
    }
}
