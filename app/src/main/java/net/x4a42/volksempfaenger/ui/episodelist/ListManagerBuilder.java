package net.x4a42.volksempfaenger.ui.episodelist;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.playlist.Playlist;
import net.x4a42.volksempfaenger.data.playlist.PlaylistProvider;
import net.x4a42.volksempfaenger.ui.main.MainActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.main.MainActivityIntentProviderBuilder;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProviderBuilder;

class ListManagerBuilder
{
    public ListManager build(Context context, Podcast podcast)
    {
        ListAdapterProxy listAdapterProxy
                = new ListAdapterProxyBuilder().build(context, podcast);

        ViewEpisodeActivityIntentProvider viewEpisodeIntentProvider
                = new ViewEpisodeActivityIntentProviderBuilder().build(context);

        EpisodeDaoWrapper episodeDao = new EpisodeDaoBuilder().build(context);

        Playlist playlist = new PlaylistProvider(context).get();

        MainActivityIntentProvider mainActivityIntentProvider =
                new MainActivityIntentProviderBuilder().build(context);

        return new ListManager(context,
                               listAdapterProxy,
                               viewEpisodeIntentProvider,
                               episodeDao,
                               playlist,
                               mainActivityIntentProvider);
    }
}
