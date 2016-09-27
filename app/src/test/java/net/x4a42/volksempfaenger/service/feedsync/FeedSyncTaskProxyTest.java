package net.x4a42.volksempfaenger.service.feedsync;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.feedparser.Feed;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.HttpURLConnection;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FeedSyncTaskProxyTest
{
    @Mock Podcast           podcast;
    @Mock HttpURLConnection feedConnection;
    @Mock FeedParserWrapper feedParser;
    @Mock PodcastUpdater    podcastUpdater;
    @Mock Feed              feed;
    FeedSyncTaskProxy       proxy;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(feedParser.parse()).thenReturn(feed);
        proxy = new FeedSyncTaskProxy(podcast, feedConnection, feedParser, podcastUpdater);
    }

    @Test
    public void doInBackground() throws Exception
    {
        proxy.doInBackground();
        Mockito.verify(podcastUpdater).update(podcast, feed);
    }
}