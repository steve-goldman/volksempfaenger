package net.x4a42.volksempfaenger.service.playlistdownload;

import android.content.Intent;

import net.x4a42.volksempfaenger.Log;

class IntentParser
{
    public interface Listener
    {
        void onRun();
    }

    private Listener listener;

    public IntentParser setListener(Listener listener)
    {
        this.listener = listener;
        return this;
    }

    public void parse(Intent intent)
    {
        if (listener == null)
        {
            return;
        }

        if (intent == null)
        {
            return;
        }

        String action = intent.getAction();
        if (action == null)
        {
            return;
        }

        switch(action)
        {
            case PlaylistDownloadService.ActionRun:
                listener.onRun();
                break;
            default:
                Log.e(this, String.format("unexpected action:%s", action));
                break;
        }
    }
}
