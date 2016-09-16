package net.x4a42.volksempfaenger.service.feedsync;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedItem;
import net.x4a42.volksempfaenger.misc.NowProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PodcastUpdaterTest
{
    @Mock PodcastDaoWrapper     podcastDao;
    @Mock NowProvider           nowProvider;
    @Mock EpisodeUpdater        episodeUpdater;
    @Mock LogoDownloaderBuilder logoDownloaderBuilder;
    @Mock LogoDownloader        logoDownloader;
    @Mock Podcast               podcast;
    long                        now       = 100;
    Feed                        feed      = new Feed();
    FeedItem                    feedItem1 = new FeedItem();
    FeedItem                    feedItem2 = new FeedItem();
    PodcastUpdater              podcastUpdater;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(nowProvider.get()).thenReturn(now);
        Mockito.when(logoDownloaderBuilder.build(podcast, feed)).thenReturn(logoDownloader);
        feed.title       = "my-title";
        feed.description = "my-description";
        feed.website     = "my-website";
        feed.items.add(feedItem1);
        feed.items.add(feedItem2);
        podcastUpdater   = new PodcastUpdater(podcastDao,
                                              nowProvider,
                                              episodeUpdater,
                                              logoDownloaderBuilder);
    }

    @Test
    public void update() throws Exception
    {
        podcastUpdater.update(podcast, feed);
        Mockito.verify(podcast).setTitle(feed.title);
        Mockito.verify(podcast).setDescription(feed.description);
        Mockito.verify(podcast).setWebsite(feed.website);
        Mockito.verify(podcast).setLastUpdate(now);
        Mockito.verify(episodeUpdater).insertOrUpdate(podcast, feedItem1);
        Mockito.verify(episodeUpdater).insertOrUpdate(podcast, feedItem2);
        Mockito.verify(logoDownloader).download();
    }
}