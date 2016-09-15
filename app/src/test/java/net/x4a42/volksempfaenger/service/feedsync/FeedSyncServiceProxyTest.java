package net.x4a42.volksempfaenger.service.feedsync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FeedSyncServiceProxyTest
{
    @Mock Context             context;
    @Mock IntentParser        intentParser;
    @Mock FeedSyncTaskBuilder taskBuilder;
    @Mock FeedSyncTask        task;
    @Mock Intent              intent;
    @Mock Podcast             podcast;
    FeedSyncServiceProxy      proxy;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(taskBuilder.build(context)).thenReturn(task);
        proxy = new FeedSyncServiceProxy(context, intentParser, taskBuilder);
    }

    @Test
    public void onCreate() throws Exception
    {
        proxy.onCreate();
    }

    @Test
    public void onDestroy() throws Exception
    {
        proxy.onDestroy();
    }

    @Test
    public void onStartCommand() throws Exception
    {
        int value = proxy.onStartCommand(intent);
        assertEquals(Service.START_STICKY, value);
        Mockito.verify(intentParser).parse(intent);
    }

    @Test
    public void onSync() throws Exception
    {
        proxy.onSync(podcast);
        Mockito.verify(task).execute(podcast);
    }
}