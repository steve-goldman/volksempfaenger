package net.x4a42.volksempfaenger.service.playback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import net.x4a42.volksempfaenger.Log;

class AudioBecomingNoisyManager extends BroadcastReceiver
{
    public interface Listener
    {
        void onAudioBecomingNoisy();
    }

    private final Context context;
    private Listener      listener;

    public AudioBecomingNoisyManager(Context context)
    {
        this.context = context;
    }

    public void start()
    {
        context.registerReceiver(this, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
    }

    public void stop()
    {
        context.unregisterReceiver(this);
    }

    public AudioBecomingNoisyManager setListener(Listener listener)
    {
        this.listener = listener;
        return this;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        switch (intent.getAction())
        {
            case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                handleAudioBecomingNoisy();
                break;

            default:
                Log.d(this, String.format("unexpected action:%s", intent.getAction()));
                break;
        }
    }

    private void handleAudioBecomingNoisy()
    {
        listener.onAudioBecomingNoisy();
    }
}
