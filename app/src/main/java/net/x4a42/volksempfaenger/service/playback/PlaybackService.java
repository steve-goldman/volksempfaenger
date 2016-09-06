package net.x4a42.volksempfaenger.service.playback;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PlaybackService extends Service
{
    private static final String ActionPrefix    = "net.x4a42.volksempfaenger.intent.action.";
    public static final  String ActionPlay      = ActionPrefix + "PLAY";
    public static final  String ActionPause     = ActionPrefix + "PAUSE";
    public static final  String ActionPlayPause = ActionPrefix + "PLAY_PAUSE";
    public static final  String ActionStop      = ActionPrefix + "STOP";
    public static final  String ActionSeek      = ActionPrefix + "SEEK";
    public static final  String ActionMove      = ActionPrefix + "MOVE";

    private PlaybackServiceProxy proxy;

    @Override
    public void onCreate()
    {
        super.onCreate();
        proxy = new PlaybackServiceProxyBuilder().build(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return proxy.onStartCommand(intent);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        proxy.onDestroy();
        proxy = null;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return proxy.onBind();
    }
}
