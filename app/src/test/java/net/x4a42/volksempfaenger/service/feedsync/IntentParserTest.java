package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Intent;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class IntentParserTest
{
    @Mock PodcastDaoWrapper     podcastDao;
    @Mock Intent                intent;
    @Mock Podcast               podcast;
    @Mock IntentParser.Listener listener;
    Long                        podcastId = 10L;
    IntentParser                intentParser;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(intent.getLongExtra(FeedSyncServiceIntentProvider.PodcastIdKey, -1))
               .thenReturn(podcastId);

        Mockito.when(podcastDao.getById(podcastId)).thenReturn(podcast);

        intentParser = new IntentParser(podcastDao)
                .setListener(listener);
    }

    @Test
    public void parseNoListener() throws Exception
    {
        intentParser.setListener(null);

        intentParser.parse(intent);

        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void parseNoIntent() throws Exception
    {
        intentParser.parse(null);

        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void parseNoAction() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(null);

        intentParser.parse(intent);

        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void parseSync() throws Exception
    {
        Mockito.when(intent.getAction()).thenReturn(FeedSyncService.ActionSync);

        intentParser.parse(intent);

        Mockito.verify(listener).onSync(podcast);
    }

}