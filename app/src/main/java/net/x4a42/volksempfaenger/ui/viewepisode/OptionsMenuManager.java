package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.x4a42.volksempfaenger.NavUtilsWrapper;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.ToastMaker;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.playlist.Playlist;
import net.x4a42.volksempfaenger.service.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventListener;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacadeProvider;
import net.x4a42.volksempfaenger.ui.settings.SettingsActivityIntentProvider;

class OptionsMenuManager implements PlaybackEventListener
{
    private final Activity                       activity;
    private final Episode                        episode;
    private final MenuInflater                   inflater;
    private final EpisodeSharer                  episodeSharer;
    private final SettingsActivityIntentProvider settingsIntentProvider;
    private final NavUtilsWrapper                navUtilsWrapper;
    private final Playlist                       playlist;
    private final ToastMaker                     toastMaker;
    private PlaybackServiceFacadeProvider        facadeProvider;

    public OptionsMenuManager(Activity                       activity,
                              Episode                        episode,
                              MenuInflater                   inflater,
                              EpisodeSharer                  episodeSharer,
                              SettingsActivityIntentProvider settingsIntentProvider,
                              NavUtilsWrapper                navUtilsWrapper,
                              Playlist                       playlist,
                              ToastMaker                     toastMaker)
    {
        this.activity               = activity;
        this.episode                = episode;
        this.inflater               = inflater;
        this.episodeSharer          = episodeSharer;
        this.settingsIntentProvider = settingsIntentProvider;
        this.navUtilsWrapper        = navUtilsWrapper;
        this.playlist               = playlist;
        this.toastMaker             = toastMaker;
    }

    public OptionsMenuManager setFacadeProvider(PlaybackServiceFacadeProvider facadeProvider)
    {
        this.facadeProvider = facadeProvider;
        return this;
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        inflater.inflate(R.menu.view_episode, menu);
        menu.findItem(R.id.item_play).setVisible(canPlay());
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.item_play:
                handlePlay();
                return true;
            case R.id.item_share:
                episodeSharer.share();
                return true;
            case R.id.item_settings:
                activity.startActivity(settingsIntentProvider.get());
                return true;
            case android.R.id.home:
                navUtilsWrapper.navigateUpFromSameTask();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onPlaybackEvent(PlaybackEvent playbackEvent)
    {
        activity.invalidateOptionsMenu();
    }

    private boolean canPlay()
    {
        PlaybackServiceFacade facade = facadeProvider.getFacade();
        return facade != null &&
                (!facade.isPlaying() || !facade.isEpisodeOpen(episode));
    }

    private void handlePlay()
    {
        if (!playlist.playEpisodeNow(episode))
        {
            toastMaker.showTextShort(activity.getString(R.string.toast_episode_enqueued));
        }
    }
}
