package net.x4a42.volksempfaenger.ui.subscriptiongrid;

import android.app.Activity;

import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDeleter;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDeleterBuilder;
import net.x4a42.volksempfaenger.ui.episodelist.EpisodeListActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.episodelist.EpisodeListActivityIntentProviderBuilder;

class GridManagerBuilder
{
    public GridManager build(Activity activity)
    {
        GridAdapterProxy gridAdapterProxy
                = new GridAdapterProxyBuilder().build(activity);

        EpisodeListActivityIntentProvider intentProvider
                = new EpisodeListActivityIntentProviderBuilder().build(activity);

        PodcastDaoWrapper podcastDao     = new PodcastDaoBuilder().build(activity);
        PodcastDeleter    podcastDeleter = new PodcastDeleterBuilder().build(activity);

        return new GridManager(activity,
                               gridAdapterProxy,
                               intentProvider,
                               podcastDao,
                               podcastDeleter);
    }
}
