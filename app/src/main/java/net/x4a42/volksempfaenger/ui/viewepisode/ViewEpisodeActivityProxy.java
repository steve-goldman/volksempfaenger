package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.NavUtilsWrapper;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.ToastMaker;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.service.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventListener;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.ui.SettingsActivity;
import net.x4a42.volksempfaenger.ui.nowplaying.NowPlayingFragment;

class ViewEpisodeActivityProxy implements OptionsMenuManager.Listener,
                                          PlaybackEventListener
{
    private final ViewEpisodeActivity              activity;
    private final Episode                          episode;
    private final FragmentManager                  fragmentManager;
    private final OptionsMenuManager               optionsMenuManager;
    private final PlaybackEventReceiver            playbackEventReceiver;
    private final Presenter                        presenter;
    private final PlaybackServiceIntentProvider    intentProvider;
    private final ToastMaker                       toastMaker;
    private final NavUtilsWrapper                  navUtilsWrapper;
    private final EpisodeSharer                    sharer;
    private final IntentBuilder                    intentBuilder;
    private final DownloadHelper                   downloadHelper;
    private final PlaybackServiceConnectionManager connectionManager;

    public ViewEpisodeActivityProxy(ViewEpisodeActivity              activity,
                                    Episode                          episode,
                                    FragmentManager                  fragmentManager,
                                    OptionsMenuManager               optionsMenuManager,
                                    PlaybackEventReceiver            playbackEventReceiver,
                                    Presenter                        presenter,
                                    PlaybackServiceIntentProvider    intentProvider,
                                    ToastMaker                       toastMaker,
                                    NavUtilsWrapper                  navUtilsWrapper,
                                    EpisodeSharer                    sharer,
                                    IntentBuilder                    intentBuilder,
                                    DownloadHelper                   downloadHelper,
                                    PlaybackServiceConnectionManager connectionManager)
    {
        this.activity              = activity;
        this.episode               = episode;
        this.fragmentManager       = fragmentManager;
        this.optionsMenuManager    = optionsMenuManager;
        this.playbackEventReceiver = playbackEventReceiver;
        this.presenter             = presenter;
        this.intentProvider        = intentProvider;
        this.toastMaker            = toastMaker;
        this.navUtilsWrapper       = navUtilsWrapper;
        this.sharer                = sharer;
        this.intentBuilder         = intentBuilder;
        this.downloadHelper        = downloadHelper;
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

    //
    // OptionsMenuManager.Listener
    //

    @Override
    public void onPlay()
    {
        activity.startService(intentProvider.getPlayIntent(episode));
    }

    @Override
    public void onDownload()
    {
        downloadHelper.download();
    }

    @Override
    public void onShare()
    {
        sharer.share();
    }

    @Override
    public void onDelete()
    {
        // TODO: delete the episode
    }

    @Override
    public void onMarkListened()
    {
        // TODO: mark listened
    }

    @Override
    public void onMarkNew()
    {
        // TODO: mark new
    }

    @Override
    public void onWebsite()
    {
        Uri uri = null; // TODO
        if (uri != null)
        {
            activity.startActivity(intentBuilder.build(Intent.ACTION_VIEW, uri));
            return;
        }

        toastMaker.showTextShort("No website");
    }

    @Override
    public void onSettings()
    {
        activity.startActivity(intentBuilder.build(activity, SettingsActivity.class));
    }

    @Override
    public void onHome()
    {
        navUtilsWrapper.navigateUpFromSameTask();
    }

    //
    // PlaybackEventListener
    //

    @Override
    public void onPlaybackEvent(PlaybackEvent playbackEvent)
    {
        activity.invalidateOptionsMenu();
    }

}
