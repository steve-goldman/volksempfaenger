package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import net.x4a42.volksempfaenger.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GridManagerTest
{
    @Mock GridAdapter    gridAdapter;
    @Mock LayoutInflater inflater;
    @Mock ViewGroup      container;
    @Mock View           view;
    @Mock GridView       gridView;
    GridManager          gridManager;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(inflater.inflate(R.layout.subscription_list, container, false))
               .thenReturn(view);
        Mockito.when(view.findViewById(R.id.grid)).thenReturn(gridView);
        gridManager = new GridManager(gridAdapter);
    }

    @Test
    public void onCreateView() throws Exception
    {
        View result = gridManager.onCreateView(inflater, container);
        assertEquals(view, result);
        Mockito.verify(gridView).setOnItemClickListener(gridManager);
        Mockito.verify(gridView).setAdapter(gridAdapter);
    }

    @Test
    public void onResume() throws Exception
    {
        gridManager.onResume();
        Mockito.verify(gridAdapter).onResume();
    }

    @Test
    public void onPause() throws Exception
    {
        gridManager.onPause();
        Mockito.verify(gridAdapter).onPause();
    }

    @Test
    public void onItemClick() throws Exception
    {

    }
}