package net.x4a42.volksempfaenger.service.playback;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

public class PlaybackServiceConnectionManager implements ServiceConnection,
                                                         PlaybackServiceFacadeProvider
{
    public interface Listener
    {
        void onPlaybackServiceConnected();
        void onPlaybackServiceDisconnected();
    }

    private final Context                       context;
    private final PlaybackServiceIntentProvider intentProvider;
    private PlaybackServiceFacade               facade;
    private Listener                            listener;

    public PlaybackServiceConnectionManager(Context                       context,
                                            PlaybackServiceIntentProvider intentProvider)
    {
        this.context        = context;
        this.intentProvider = intentProvider;
    }

    public PlaybackServiceConnectionManager setListener(Listener listener)
    {
        this.listener = listener;
        return this;
    }

    public void onCreate()
    {
        context.bindService(intentProvider.getBindIntent(), this, Context.BIND_AUTO_CREATE);
    }

    public void onDestroy()
    {
        context.unbindService(this);
    }

    @Override
    public PlaybackServiceFacade getFacade()
    {
        return facade;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service)
    {
        facade = ((PlaybackServiceBinder) service).getFacade();
        if (listener != null)
        {
            listener.onPlaybackServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name)
    {
        facade = null;
        if (listener != null)
        {
            listener.onPlaybackServiceDisconnected();
        }
    }
}
