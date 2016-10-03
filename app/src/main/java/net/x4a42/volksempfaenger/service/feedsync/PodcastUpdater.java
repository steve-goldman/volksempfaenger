package net.x4a42.volksempfaenger.service.feedsync;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedItem;

import java.io.IOException;

class PodcastUpdater
{
    private final PodcastDaoWrapper     podcastDao;
    private final EpisodeUpdater        episodeUpdater;
    private final LogoDownloaderBuilder logoDownloaderBuilder;

    public PodcastUpdater(PodcastDaoWrapper     podcastDao,
                          EpisodeUpdater        episodeUpdater,
                          LogoDownloaderBuilder logoDownloaderBuilder)
    {
        this.podcastDao            = podcastDao;
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
        podcastDao.update(podcast, feed.title, feed.description, feed.website);
        logoDownloaderBuilder.build(podcast, feed).download();
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
