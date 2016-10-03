package net.x4a42.volksempfaenger.service.feedsync;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PodcastUpdaterTest
{
    @Mock PodcastDaoWrapper     podcastDao;
    @Mock EpisodeUpdater        episodeUpdater;
    @Mock LogoDownloaderBuilder logoDownloaderBuilder;
    @Mock LogoDownloader        logoDownloader;
    @Mock Podcast               podcast;
    @Mock List<Episode>         episodes;
    Feed                        feed      = new Feed();
    FeedItem                    feedItem1 = new FeedItem();
    FeedItem                    feedItem2 = new FeedItem();
    PodcastUpdater              podcastUpdater;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(logoDownloaderBuilder.build(podcast, feed)).thenReturn(logoDownloader);
        Mockito.when(podcast.getEpisodes()).thenReturn(episodes);
        feed.title       = "my-title";
        feed.description = "my-description";
        feed.website     = "my-website";
        feed.items.add(feedItem1);
        feed.items.add(feedItem2);
        podcastUpdater   = new PodcastUpdater(podcastDao,
                                              episodeUpdater,
                                              logoDownloaderBuilder);
    }

    @Test
    public void updateFirstSync() throws Exception
    {
        Mockito.when(episodes.isEmpty()).thenReturn(true);
        podcastUpdater.update(podcast, feed);
        Mockito.verify(podcastDao).update(podcast,
                                          feed.title,
                                          feed.description,
                                          feed.website);
        Mockito.verify(episodeUpdater).insertOrUpdate(podcast, feedItem1, true);
        Mockito.verify(episodeUpdater).insertOrUpdate(podcast, feedItem2, true);
        Mockito.verify(logoDownloader).download();
    }

    @Test
    public void updateNotFirstSync() throws Exception
    {
        podcastUpdater.update(podcast, feed);
        Mockito.verify(podcastDao).update(podcast,
                                          feed.title,
                                          feed.description,
                                          feed.website);
        Mockito.verify(episodeUpdater).insertOrUpdate(podcast, feedItem1, false);
        Mockito.verify(episodeUpdater).insertOrUpdate(podcast, feedItem2, false);
        Mockito.verify(logoDownloader).download();
    }
}