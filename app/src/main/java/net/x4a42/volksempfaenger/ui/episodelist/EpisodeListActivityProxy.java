package net.x4a42.volksempfaenger.ui.episodelist;

import android.app.Activity;
import android.app.FragmentManager;
import android.view.View;

import net.x4a42.volksempfaenger.R;

class EpisodeListActivityProxy
{
    private final Activity        activity;
    private final ListManager     listManager;

    public EpisodeListActivityProxy(Activity        activity,
                                    ListManager     listManager)
    {
        this.activity        = activity;
        this.listManager     = listManager;
    }

    public void onCreate()
    {
        activity.setContentView(R.layout.episode_list_default);
        View view = activity.findViewById(android.R.id.content);
        listManager.init(view);
    }

    public void onResume()
    {
        listManager.refresh();
    }

    public void onPause()
    {
        listManager.clear();
    }
}
