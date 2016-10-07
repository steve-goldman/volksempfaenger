package net.x4a42.volksempfaenger.service.syncall;

import android.content.Intent;

import net.x4a42.volksempfaenger.Log;

class IntentParser
{
    public interface Listener
    {
        void onSync();
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

        switch (action)
        {
            case SyncAllService.ActionSync:
                listener.onSync();
                break;
            default:
                Log.d(this, String.format("unexpected action:%s", action));
                break;
        }
    }
}
