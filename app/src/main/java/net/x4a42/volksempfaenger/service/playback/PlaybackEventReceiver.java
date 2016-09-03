package net.x4a42.volksempfaenger.service.playback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
    This is the companion class to PlaybackEventBroadcaster. It listens for PlaybackEvents
    over broadcast and forwards them to the associated PlaybackEventListener.
 */

public class PlaybackEventReceiver extends BroadcastReceiver
{
    private final Context                   context;
    private final PlaybackEventActionMapper playbackEventActionMapper;
    private PlaybackEventListener           listener;

    public PlaybackEventReceiver(Context                   context,
                                 PlaybackEventActionMapper playbackEventActionMapper)
    {
        this.context                   = context;
        this.playbackEventActionMapper = playbackEventActionMapper;
    }

    public PlaybackEventReceiver setListener(PlaybackEventListener listener)
    {
        this.listener                  = listener;
        return this;
    }

    public void subscribe()
    {
        context.registerReceiver(this, playbackEventActionMapper.getIntentFilter());
    }

    public void unsubscribe()
    {
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if (playbackEventActionMapper.isValid(action))
        {
            PlaybackEvent playbackEvent = playbackEventActionMapper.getEvent(action);
            listener.onPlaybackEvent(playbackEvent);
        }
    }
}
