package net.x4a42.volksempfaenger.event.connectivitychanged;

import org.greenrobot.eventbus.EventBus;

public class ConnectivityChangedEventReceiverBuilder
{
    public ConnectivityChangedEventReceiver build()
    {
        return new ConnectivityChangedEventReceiver(EventBus.getDefault());
    }
}
