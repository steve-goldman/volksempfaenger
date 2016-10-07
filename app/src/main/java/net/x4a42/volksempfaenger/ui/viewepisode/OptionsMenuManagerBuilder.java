package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.Activity;

import net.x4a42.volksempfaenger.NavUtilsWrapper;
import net.x4a42.volksempfaenger.ToastMaker;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.playlist.Playlist;
import net.x4a42.volksempfaenger.data.playlist.PlaylistProvider;
import net.x4a42.volksempfaenger.ui.settings.SettingsActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.settings.SettingsActivityIntentProviderBuilder;

class OptionsMenuManagerBuilder
{
    public OptionsMenuManager build(Activity activity, Episode episode)
    {
        EpisodeSharer episodeSharer
                = new EpisodeSharerBuilder().build(activity, episode);

        SettingsActivityIntentProvider settingsIntentProvider
                = new SettingsActivityIntentProviderBuilder().build(activity);

        NavUtilsWrapper navUtilsWrapper = new NavUtilsWrapper(activity);
        Playlist        playlist        = new PlaylistProvider(activity).get();
        ToastMaker      toastMaker      = new ToastMaker(activity);

        return new OptionsMenuManager(activity,
                                      episode,
                                      activity.getMenuInflater(),
                                      episodeSharer,
                                      settingsIntentProvider,
                                      navUtilsWrapper,
                                      playlist,
                                      toastMaker);
    }
}
