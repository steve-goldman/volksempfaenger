package net.x4a42.volksempfaenger.service.feedsync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class FeedSyncService extends Service
{
    private static final String ActionPrefix = "net.x4a42.volksempfaenger.intent.feedsync.";
    public static final  String ActionSync   = ActionPrefix + "SYNC";
    private FeedSyncServiceProxy proxy;

    @Override
    public void onCreate()
    {
        super.onCreate();
        proxy = new FeedSyncServiceProxyBuilder().build(this);
        proxy.onCreate();
    }

    @Override
    public void onDestroy()
    {
        proxy.onDestroy();
        proxy = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return proxy.onStartCommand(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
