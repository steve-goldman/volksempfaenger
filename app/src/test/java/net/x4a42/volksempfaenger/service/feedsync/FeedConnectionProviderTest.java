package net.x4a42.volksempfaenger.service.feedsync;

import net.x4a42.volksempfaenger.misc.HttpURLConnectionProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FeedConnectionProviderTest
{
    @Mock HttpURLConnectionProvider connectionProvider;
    @Mock HttpURLConnection         connection;
    int                             connectTimeout = 10;
    FeedConnectionProvider          feedConnectionProvider;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(connectionProvider.get()).thenReturn(connection);
        feedConnectionProvider = new FeedConnectionProvider(connectionProvider, connectTimeout);
    }

    @Test
    public void get() throws Exception
    {
        HttpURLConnection newConnection = feedConnectionProvider.get();
        assertEquals(connection, newConnection);
        Mockito.verify(connection).setRequestProperty(Mockito.eq("User-Agent"), Mockito.anyString());
        Mockito.verify(connection).setInstanceFollowRedirects(true);
        Mockito.verify(connection).setRequestProperty("Accept", FeedConnectionProvider.ACCEPT);
    }
}
