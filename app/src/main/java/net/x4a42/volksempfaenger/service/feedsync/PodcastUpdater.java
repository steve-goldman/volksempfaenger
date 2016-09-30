package net.x4a42.volksempfaenger.service.feedsync;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedItem;
import net.x4a42.volksempfaenger.misc.NowProvider;

import java.io.IOException;

class PodcastUpdater
{
    private final PodcastDaoWrapper     podcastDao;
    private final NowProvider           nowProvider;
    private final EpisodeUpdater        episodeUpdater;
    private final LogoDownloaderBuilder logoDownloaderBuilder;

    public PodcastUpdater(PodcastDaoWrapper     podcastDao,
                          NowProvider           nowProvider,
                          EpisodeUpdater        episodeUpdater,
                          LogoDownloaderBuilder logoDownloaderBuilder)
    {
        this.podcastDao            = podcastDao;
        this.nowProvider           = nowProvider;
        this.episodeUpdater        = episodeUpdater;
        this.logoDownloaderBuilder = logoDownloaderBuilder;
    }

    public void update(Podcast podcast, Feed feed) throws IOException
    {
        updatePodcast(podcast, feed);
        updateEpisodes(podcast, feed);
    }

    private void updatePodcast(Podcast podcast, Feed feed) throws IOException
    {
        podcast.setTitle(feed.title);
        podcast.setDescription(feed.description);
        podcast.setWebsite(feed.website);
        podcast.setLastUpdate(nowProvider.get());
        logoDownloaderBuilder.build(podcast, feed).download();

        podcastDao.update(podcast);
    }

    private void updateEpisodes(Podcast podcast, Feed feed)
    {
        boolean firstSync = podcast.getEpisodes().isEmpty();
        for (FeedItem feedItem : feed.items)
        {
            episodeUpdater.insertOrUpdate(podcast, feedItem, firstSync);
        }
    }
}
