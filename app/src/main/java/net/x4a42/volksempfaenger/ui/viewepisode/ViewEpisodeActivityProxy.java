package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;

import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.NavUtilsWrapper;
import net.x4a42.volksempfaenger.ToastMaker;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.episode.EpisodeCursorLoader;
import net.x4a42.volksempfaenger.data.episode.EpisodeCursorProvider;
import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelper;
import net.x4a42.volksempfaenger.service.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventListener;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceBinder;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacadeProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.ui.SettingsActivity;

class ViewEpisodeActivityProxy implements OptionsMenuManager.Listener,
                                                 EpisodeCursorProvider,
                                                 PlaybackServiceFacadeProvider,
                                                 PlaybackEventListener,
                                                 ServiceConnection,
                                                 EpisodeCursorLoader.Listener
{
    private final ViewEpisodeActivity           activity;
    private final Uri                           episodeUri;
    private final OptionsMenuManager            optionsMenuManager;
    private final PlaybackEventReceiver         playbackEventReceiver;
    private final EpisodeCursorLoader           episodeCursorLoader;
    private final Presenter                     presenter;
    private final PlaybackServiceIntentProvider intentProvider;
    private final EpisodeDataHelper             episodeDataHelper;
    private final ToastMaker                    toastMaker;
    private final NavUtilsWrapper               navUtilsWrapper;
    private final EpisodeSharer                 sharer;
    private final IntentBuilder                 intentBuilder;
    private final DownloadHelper                downloadHelper;
    private EpisodeCursor                       episodeCursor;
    private PlaybackServiceFacade               playbackServiceFacade;

    public ViewEpisodeActivityProxy(ViewEpisodeActivity           activity,
                                    Uri                           episodeUri,
                                    OptionsMenuManager            optionsMenuManager,
                                    PlaybackEventReceiver         playbackEventReceiver,
                                    EpisodeCursorLoader           episodeCursorLoader,
                                    Presenter                     presenter,
                                    PlaybackServiceIntentProvider intentProvider,
                                    EpisodeDataHelper             episodeDataHelper,
                                    ToastMaker                    toastMaker,
                                    NavUtilsWrapper               navUtilsWrapper,
                                    EpisodeSharer                 sharer,
                                    IntentBuilder                 intentBuilder,
                                    DownloadHelper                downloadHelper)
    {
        this.activity              = activity;
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
    }

    public void onCreate()
    {
        startPlaybackService();
        episodeCursorLoader.init();
        presenter.onCreate();
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
        activity.unbindService(this);
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
    // PlaybackServiceFacadeProvider
    //

    @Override
    public PlaybackServiceFacade getFacade()
    {
        return playbackServiceFacade;
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
    // ServiceConnection
    //

    @Override
    public void onServiceConnected(ComponentName name, IBinder service)
    {
        playbackServiceFacade = ((PlaybackServiceBinder) service).getFacade();
        activity.invalidateOptionsMenu();
    }

    @Override
    public void onServiceDisconnected(ComponentName name)
    {
        playbackServiceFacade = null;
        activity.invalidateOptionsMenu();
        startPlaybackService();
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

    //
    // helper methods
    //

    private void startPlaybackService()
    {
        activity.bindService(intentProvider.getBindIntent(), this, Activity.BIND_AUTO_CREATE);
    }

}
