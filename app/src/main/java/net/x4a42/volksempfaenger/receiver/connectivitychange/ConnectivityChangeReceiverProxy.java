package net.x4a42.volksempfaenger.receiver.connectivitychange;

import android.content.Intent;
import android.net.ConnectivityManager;

import net.x4a42.volksempfaenger.event.connectivitychanged.ConnectivityChangedEventBroadcaster;
import net.x4a42.volksempfaenger.event.connectivitychanged.ConnectivityChangedEventProvider;

class ConnectivityChangeReceiverProxy
{
    private final ConnectivityChangedEventBroadcaster broadcaster;
    private final ConnectivityChangedEventProvider    eventProvider;

    public ConnectivityChangeReceiverProxy(ConnectivityChangedEventBroadcaster broadcaster,
                                           ConnectivityChangedEventProvider    eventProvider)
    {
        this.broadcaster   = broadcaster;
        this.eventProvider = eventProvider;
    }

    public void onReceive(Intent intent)
    {
        if (!ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()))
        {
            return;
        }

        broadcaster.broadcast(eventProvider.get());
    }
}
