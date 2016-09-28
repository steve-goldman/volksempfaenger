package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.x4a42.volksempfaenger.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class GridViewManagerTest
{
    @Mock GridViewHolderBuilder builder;
    @Mock LayoutInflater        inflater;
    @Mock GridViewHolder        viewHolder;
    @Mock View                  convertView;
    @Mock View                  newView;
    @Mock ViewGroup             parent;
    GridViewManager             gridViewManager;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(inflater.inflate(R.layout.subscription_list_row, parent, false))
               .thenReturn(newView);
        Mockito.when(builder.build(newView)).thenReturn(viewHolder);
        Mockito.when(convertView.getTag()).thenReturn(viewHolder);
        Mockito.when(newView.getTag()).thenReturn(viewHolder);

        gridViewManager = new GridViewManager(builder, inflater);
    }

    @Test
    public void getViewHolderNoReuse() throws Exception
    {
        GridViewHolder result = gridViewManager.getViewHolder(null, parent);
        assertEquals(viewHolder, result);
        Mockito.verify(builder).build(newView);
        Mockito.verify(newView).setTag(viewHolder);
    }

    @Test
    public void getViewHolderReuse() throws Exception
    {
        GridViewHolder result = gridViewManager.getViewHolder(convertView, parent);
        assertEquals(viewHolder, result);
        Mockito.verifyNoMoreInteractions(newView, builder);
    }
}