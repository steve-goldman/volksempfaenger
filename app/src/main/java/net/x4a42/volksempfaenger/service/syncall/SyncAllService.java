package net.x4a42.volksempfaenger.service.syncall;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class SyncAllService extends Service
{
    private static final String ActionPrefix = "net.x4a42.volksempfaenger.intent.syncall.";
    public static final  String ActionSync   = ActionPrefix + "SYNC";
    private SyncAllServiceProxy proxy;

    @Override
    public void onCreate()
    {
        super.onCreate();
        proxy = new SyncAllServiceProxyBuilder().build(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
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
