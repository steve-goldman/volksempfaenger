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
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.episode.EpisodeCursorLoader;
import net.x4a42.volksempfaenger.data.episode.EpisodeCursorProvider;
import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelper;
import net.x4a42.volksempfaenger.service.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventListener;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.ui.SettingsActivity;
import net.x4a42.volksempfaenger.ui.nowplaying.NowPlayingFragment;

class ViewEpisodeActivityProxy implements OptionsMenuManager.Listener,
                                          EpisodeCursorProvider,
                                          PlaybackEventListener,
                                          EpisodeCursorLoader.Listener
{
    private final ViewEpisodeActivity              activity;
    private final FragmentManager                  fragmentManager;
    private final Uri                              episodeUri;
    private final OptionsMenuManager               optionsMenuManager;
    private final PlaybackEventReceiver            playbackEventReceiver;
    private final EpisodeCursorLoader              episodeCursorLoader;
    private final Presenter                        presenter;
    private final PlaybackServiceIntentProvider    intentProvider;
    private final EpisodeDataHelper                episodeDataHelper;
    private final ToastMaker                       toastMaker;
    private final NavUtilsWrapper                  navUtilsWrapper;
    private final EpisodeSharer                    sharer;
    private final IntentBuilder                    intentBuilder;
    private final DownloadHelper                   downloadHelper;
    private final PlaybackServiceConnectionManager connectionManager;
    private EpisodeCursor                          episodeCursor;

    public ViewEpisodeActivityProxy(ViewEpisodeActivity              activity,
                                    FragmentManager                  fragmentManager,
                                    Uri                              episodeUri,
                                    OptionsMenuManager               optionsMenuManager,
                                    PlaybackEventReceiver            playbackEventReceiver,
                                    EpisodeCursorLoader              episodeCursorLoader,
                                    Presenter                        presenter,
                                    PlaybackServiceIntentProvider    intentProvider,
                                    EpisodeDataHelper                episodeDataHelper,
                                    ToastMaker                       toastMaker,
                                    NavUtilsWrapper                  navUtilsWrapper,
                                    EpisodeSharer                    sharer,
                                    IntentBuilder                    intentBuilder,
                                    DownloadHelper                   downloadHelper,
                                    PlaybackServiceConnectionManager connectionManager)
    {
        this.activity              = activity;
        this.fragmentManager       = fragmentManager;
        this.episodeUri            = episodeUri;
        this.optionsMenuManager    = optionsMenuManager;
        this.playbackEventReceiver = playbackEventReceiver;
        this.episodeCursorLoader   = episodeCursorLoader;
        this.presenter             = presenter;
        this.intentProvider        = intentProvider;
        this.episodeDataHelper     = episodeDataHelper;
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

        episodeCursorLoader.init();
        presenter.onCreate();
        connectionManager.onCreate();

        NowPlayingFragment fragment
                = (NowPlayingFragment) fragmentManager.findFragmentById(R.id.nowplaying);
        fragment.setEpisodeUri(episodeUri);
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
        activity.startService(intentProvider.getPlayIntent(episodeUri));
    }

    @Override
    public void onDownload()
    {
        downloadHelper.download(episodeCursor);
    }

    @Override
    public void onShare()
    {
        sharer.share(episodeCursor);
    }

    @Override
    public void onDelete()
    {
        episodeDataHelper.delete(episodeUri, episodeCursor.getId());
    }

    @Override
    public void onMarkListened()
    {
        episodeDataHelper.markListened(episodeUri);
    }

    @Override
    public void onMarkNew()
    {
        episodeDataHelper.markNew(episodeUri);
    }

    @Override
    public void onWebsite()
    {
        Uri uri = episodeCursor.getUrlUri();
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
    // EpisodeCursorProvider
    //

    @Override
    public EpisodeCursor getEpisodeCursor()
    {
        return episodeCursor;
    }

    //
    // PlaybackEventListener
    //

    @Override
    public void onPlaybackEvent(PlaybackEvent playbackEvent)
    {
        activity.invalidateOptionsMenu();
    }

    //
    // EpisodeCursorLoader.Listener
    //

    @Override
    public void onCursorLoaded(EpisodeCursor episodeCursor)
    {
        this.episodeCursor = episodeCursor;
        activity.invalidateOptionsMenu();
        presenter.update(episodeCursor);
    }

    @Override
    public void onCursorReset()
    {
        this.episodeCursor = null;
        activity.invalidateOptionsMenu();
    }

}
