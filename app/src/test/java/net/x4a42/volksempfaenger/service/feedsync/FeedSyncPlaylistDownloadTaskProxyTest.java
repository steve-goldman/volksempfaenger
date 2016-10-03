package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.service.playlistdownload.PlaylistDownloadServiceIntentProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.HttpURLConnection;

@RunWith(MockitoJUnitRunner.class)
public class FeedSyncPlaylistDownloadTaskProxyTest
{
    @Mock Context                               context;
    @Mock Podcast                               podcast;
    @Mock HttpURLConnection                     feedConnection;
    @Mock FeedParserWrapper                     feedParser;
    @Mock PodcastUpdater                        podcastUpdater;
    @Mock Feed                                  feed;
    @Mock
    PlaylistDownloadServiceIntentProvider intentProvider;
    FeedSyncTaskProxy                           proxy;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(feedParser.parse()).thenReturn(feed);
        proxy = new FeedSyncTaskProxy(context,
                                      podcast,
                                      feedConnection,
                                      feedParser,
                                      podcastUpdater,
                                      intentProvider);
    }

    @Test
    public void doInBackground() throws Exception
    {
        proxy.doInBackground();
        Mockito.verify(podcastUpdater).update(podcast, feed);
    }
}