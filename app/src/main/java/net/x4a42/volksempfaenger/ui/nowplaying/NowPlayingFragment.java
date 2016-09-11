package net.x4a42.volksempfaenger.ui.nowplaying;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NowPlayingFragment extends Fragment
{
    private NowPlayingFragmentProxy proxy;

    public void setEpisodeUri(Uri episodeUri)
    {
        proxy.setEpisodeUri(episodeUri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        proxy = new NowPlayingFragmentProxyBuilder().build(this);
        proxy.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {

        return proxy.onCreateView(inflater, container);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        proxy.onStart();
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
        proxy = null;
    }

}
