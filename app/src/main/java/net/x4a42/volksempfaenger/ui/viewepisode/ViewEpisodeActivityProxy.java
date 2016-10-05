package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.ui.nowplaying.NowPlayingFragment;

class ViewEpisodeActivityProxy
{
    private final ViewEpisodeActivity              activity;
    private final Episode                          episode;
    private final FragmentManager                  fragmentManager;
    private final OptionsMenuManager               optionsMenuManager;
    private final PlaybackEventReceiver playbackEventReceiver;
    private final Presenter                        presenter;
    private final PlaybackServiceConnectionManager connectionManager;

    public ViewEpisodeActivityProxy(ViewEpisodeActivity              activity,
                                    Episode                          episode,
                                    FragmentManager                  fragmentManager,
                                    OptionsMenuManager               optionsMenuManager,
                                    PlaybackEventReceiver            playbackEventReceiver,
                                    Presenter                        presenter,
                                    PlaybackServiceConnectionManager connectionManager)
    {
        this.activity              = activity;
        this.episode               = episode;
        this.fragmentManager       = fragmentManager;
        this.optionsMenuManager    = optionsMenuManager;
        this.playbackEventReceiver = playbackEventReceiver;
        this.presenter             = presenter;
        this.connectionManager     = connectionManager;
    }

    public void onCreate()
    {
        activity.setContentView(R.layout.view_episode);

        presenter.onCreate();
        connectionManager.onCreate();

        NowPlayingFragment fragment
                = (NowPlayingFragment) fragmentManager.findFragmentById(R.id.nowplaying);
        fragment.setEpisode(episode);
    }

    public void onResume()
    {
        playbackEventReceiver.subscribe();
    }

    public void onPause()
    {
        playbackEventReceiver.unsubscribe();
    }

    public void onDestroy()
    {
        connectionManager.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        return optionsMenuManager.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        return optionsMenuManager.onOptionsItemSelected(item);
    }

}
