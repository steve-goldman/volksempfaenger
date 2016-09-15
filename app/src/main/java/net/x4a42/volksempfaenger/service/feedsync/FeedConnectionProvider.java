package net.x4a42.volksempfaenger.service.feedsync;

import net.x4a42.volksempfaenger.misc.HttpURLConnectionProvider;

import java.io.IOException;
import java.net.HttpURLConnection;

class FeedConnectionProvider
{
    public static final String ACCEPT
            = "application/atom+xml, application/rss+xml, application/xml, text/xml";

    public static final String USER_AGENT
            = FeedConnectionProvider.class.getPackage().getName();

    private final HttpURLConnectionProvider connectionProvider;
    private final int                       connectTimeout;

    public FeedConnectionProvider(HttpURLConnectionProvider connectionProvider,
                                  int                       connectTimeout)
    {
        this.connectionProvider = connectionProvider;
        this.connectTimeout     = connectTimeout;
    }

    public HttpURLConnection get()
    {
        try
        {
            HttpURLConnection connection = connectionProvider.get();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(connectTimeout);
            connection.setRequestProperty("Accept", ACCEPT);
            return connection;
        }
        catch (IOException e)
        {
            // TODO
            throw new Error(e);
        }
    }
}
