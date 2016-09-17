package net.x4a42.volksempfaenger.misc;

import android.content.Context;
import android.net.http.HttpResponseCache;

import net.x4a42.volksempfaenger.Log;

import java.io.File;
import java.io.IOException;

class HttpResponseCacheWrapper
{
    private final Context            context;
    private static HttpResponseCache cache;

    public HttpResponseCacheWrapper(Context context)
    {
        this.context = context;
    }

    public synchronized void init()
    {
        if (cache != null)
        {
            return;
        }

        try
        {
            File httpCacheDir  = new File(context.getCacheDir(), "http");
            Log.d(this, "cache dir: " + context.getCacheDir().getAbsolutePath());
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            cache = HttpResponseCache.install(httpCacheDir, httpCacheSize);
        }
        catch (IOException e)
        {
            Log.e(this, "unable to install http cache", e);
            e.printStackTrace();
        }
    }
}
