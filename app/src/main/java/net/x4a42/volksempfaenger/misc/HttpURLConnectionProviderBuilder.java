package net.x4a42.volksempfaenger.misc;

import android.content.Context;

import java.net.URL;

public class HttpURLConnectionProviderBuilder
{
    public HttpURLConnectionProvider build(Context context, URL url)
    {
        HttpResponseCacheWrapper cache = new HttpResponseCacheWrapper(context);

        return new HttpURLConnectionProvider(cache, url);
    }
}
