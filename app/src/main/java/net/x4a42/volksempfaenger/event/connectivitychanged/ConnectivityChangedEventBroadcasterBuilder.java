package net.x4a42.volksempfaenger.event.connectivitychanged;

import org.greenrobot.eventbus.EventBus;

public class ConnectivityChangedEventBroadcasterBuilder
{
    public ConnectivityChangedEventBroadcaster build()
    {
        return new ConnectivityChangedEventBroadcaster(EventBus.getDefault());
    }
}
