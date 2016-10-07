package net.x4a42.volksempfaenger.ui.playlist;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlaylistFragment extends Fragment
{
    private PlaylistFragmentProxy proxy;

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        proxy = new PlaylistFragmentProxyBuilder().build(this);
        proxy.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         bundle)
    {
        return proxy.onCreateView(inflater, container);
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
