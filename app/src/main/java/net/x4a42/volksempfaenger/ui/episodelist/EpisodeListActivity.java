package net.x4a42.volksempfaenger.ui.episodelist;

import android.app.Activity;
import android.os.Bundle;

public class EpisodeListActivity extends Activity
{
    private EpisodeListActivityProxy proxy;

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        proxy = new EpisodeListActivityProxyBuilder().build(this);
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
        proxy = null;
    }

}
