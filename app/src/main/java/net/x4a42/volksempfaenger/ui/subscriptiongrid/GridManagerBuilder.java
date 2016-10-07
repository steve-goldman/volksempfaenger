package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;
import net.x4a42.volksempfaenger.ui.episodelist.EpisodeListActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.episodelist.EpisodeListActivityIntentProviderBuilder;

class GridManagerBuilder
{
    public GridManager build(Context context)
    {
        GridAdapterProxy gridAdapterProxy
                = new GridAdapterProxyBuilder().build(context);

        EpisodeListActivityIntentProvider intentProvider
                = new EpisodeListActivityIntentProviderBuilder().build(context);

        PodcastDaoWrapper podcastDao = new PodcastDaoBuilder().build(context);

        return new GridManager(context, gridAdapterProxy, intentProvider, podcastDao);
    }
}
