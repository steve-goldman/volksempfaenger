package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;

class PodcastUpdaterBuilder
{
    public PodcastUpdater build(Context context)
    {
        PodcastDaoWrapper     podcastDao            = new PodcastDaoBuilder().build(context);
        EpisodeUpdater        episodeUpdater        = new EpisodeUpdaterBuilder().build(context);
        LogoDownloaderBuilder logoDownloaderBuilder = new LogoDownloaderBuilder(context);

        return new PodcastUpdater(podcastDao, episodeUpdater, logoDownloaderBuilder);
    }
}
