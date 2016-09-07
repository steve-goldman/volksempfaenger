package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ViewEpisodeActivity extends Activity
{
    private ViewEpisodeActivityProxy proxy;

    public ViewEpisodeActivityProxy getProxy()
    {
        return proxy;
    }

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        proxy = new ViewEpisodeActivityProxyBuilder().build(this);
        proxy.onCreate();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        proxy.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        proxy.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        proxy.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return proxy.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return proxy.onOptionsItemSelected(item);
    }
}
