package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;
import android.content.Intent;

import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FeedSyncServiceIntentProviderTest
{
    @Mock Context                 context;
    @Mock IntentBuilder           intentBuilder;
    @Mock Intent                  syncIntent;
    @Mock Podcast                 podcast;
    Long podcastId                = 10L;
    FeedSyncServiceIntentProvider intentProvider;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(podcast.get_id()).thenReturn(podcastId);
        Mockito.when(intentBuilder.build(context, FeedSyncService.class)).thenReturn(syncIntent);
        Mockito.when(syncIntent.setAction(FeedSyncService.ActionSync)).thenReturn(syncIntent);
        Mockito.when(syncIntent.putExtra(FeedSyncServiceIntentProvider.PodcastIdKey, podcastId))
               .thenReturn(syncIntent);

        intentProvider = new FeedSyncServiceIntentProvider(context, intentBuilder);
    }

    @Test
    public void getSyncIntent() throws Exception
    {
        Intent newIntent = intentProvider.getSyncIntent(podcast);
        assertEquals(syncIntent, newIntent);
        Mockito.verify(syncIntent).setAction(FeedSyncService.ActionSync);
        Mockito.verify(syncIntent).putExtra(FeedSyncServiceIntentProvider.PodcastIdKey, podcastId);
    }
}