package net.x4a42.volksempfaenger.service.playlistdownload;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class PlaylistDownloadService extends Service
{
    private static final String ActionPrefix = "net.x4a42.volksempfaenger.intent.playlistdownload.";
    public static final  String ActionRun    = ActionPrefix + "RUN";
    private PlaylistDownloadServiceProxy proxy;

    @Override
    public void onCreate()
    {
        super.onCreate();
        proxy = new PlaylistDownloadServiceProxyBuilder().build(this);
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
