package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.misc.HttpURLConnectionProvider;

import java.net.MalformedURLException;
import java.net.URL;

class LogoConnectionProviderBuilder
{
    public LogoConnectionProvider build(Context context, String logoUrl)
    {
        try
        {
            URL                       url                = new URL(logoUrl);
            HttpURLConnectionProvider connectionProvider = new HttpURLConnectionProvider(url);
            int                       connectTimeout     = context.getResources()
                                                                  .getInteger(R.integer.logo_download_connect_timeout);

            return new LogoConnectionProvider(connectionProvider, connectTimeout);
        }
        catch (MalformedURLException e)
        {
            throw new Error(e);
        }
    }
}
