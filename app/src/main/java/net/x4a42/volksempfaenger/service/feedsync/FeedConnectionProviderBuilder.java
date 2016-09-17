package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.misc.HttpURLConnectionProvider;
import net.x4a42.volksempfaenger.misc.HttpURLConnectionProviderBuilder;

import java.net.MalformedURLException;
import java.net.URL;

class FeedConnectionProviderBuilder
{
    public FeedConnectionProvider build(Context context, String feedUrl)
    {
        try
        {
            URL url            = new URL(feedUrl);
            int connectTimeout = context.getResources().getInteger(R.integer.feed_download_connect_timeout);

            HttpURLConnectionProvider connectionProvider
                    = new HttpURLConnectionProviderBuilder().build(context, url);

            return new FeedConnectionProvider(connectionProvider, connectTimeout);
        }
        catch (MalformedURLException e)
        {
            throw new Error(e);
        }
    }
}
