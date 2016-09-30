package net.x4a42.volksempfaenger.ui.episodelist;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProviderBuilder;

class ListManagerBuilder
{
    public ListManager build(Context context, Podcast podcast)
    {
        ListAdapterProxy listAdapterProxy
                = new ListAdapterProxyBuilder().build(context, podcast);

        ViewEpisodeActivityIntentProvider intentProvider
                = new ViewEpisodeActivityIntentProviderBuilder().build(context);

        EpisodeDaoWrapper episodeDao = new EpisodeDaoBuilder().build(context);

        return new ListManager(context,
                               listAdapterProxy,
                               intentProvider,
                               episodeDao);
    }
}
