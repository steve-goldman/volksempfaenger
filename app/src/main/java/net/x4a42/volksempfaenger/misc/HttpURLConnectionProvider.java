package net.x4a42.volksempfaenger.misc;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConnectionProvider
{
    private final HttpResponseCacheWrapper cache;
    private final URL                      url;

    public HttpURLConnectionProvider(HttpResponseCacheWrapper cache,
                                     URL                      url)
    {
        this.cache = cache;
        this.url   = url;
    }

    public HttpURLConnection get() throws IOException
    {
        cache.init();
        return (HttpURLConnection) url.openConnection();
    }
}
