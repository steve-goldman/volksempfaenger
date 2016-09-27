package net.x4a42.volksempfaenger.service.feedsync;

import net.x4a42.volksempfaenger.misc.HttpURLConnectionProvider;

import java.io.IOException;
import java.net.HttpURLConnection;

class LogoConnectionProvider
{
    private final HttpURLConnectionProvider connectionProvider;
    private final int                       connectTimeout;

    public LogoConnectionProvider(HttpURLConnectionProvider connectionProvider,
                                  int                       connectTimeout)
    {
        this.connectionProvider = connectionProvider;
        this.connectTimeout     = connectTimeout;
    }

    public HttpURLConnection get() throws IOException
    {
        HttpURLConnection connection = connectionProvider.get();
        connection.setConnectTimeout(connectTimeout);
        return connection;
    }
}
