package net.x4a42.volksempfaenger.misc;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConnectionProvider
{
    private final URL url;

    public HttpURLConnectionProvider(URL url)
    {
        this.url = url;
    }

    public HttpURLConnection get() throws IOException
    {
        return (HttpURLConnection) url.openConnection();
    }
}
