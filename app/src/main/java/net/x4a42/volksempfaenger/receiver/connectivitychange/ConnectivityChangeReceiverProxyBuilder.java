package net.x4a42.volksempfaenger.receiver.connectivitychange;

import net.x4a42.volksempfaenger.event.connectivitychanged.ConnectivityChangedEventBroadcaster;
import net.x4a42.volksempfaenger.event.connectivitychanged.ConnectivityChangedEventBroadcasterBuilder;
import net.x4a42.volksempfaenger.event.connectivitychanged.ConnectivityChangedEventProvider;

class ConnectivityChangeReceiverProxyBuilder
{
    public ConnectivityChangeReceiverProxy build()
    {
        ConnectivityChangedEventBroadcaster broadcaster
                = new ConnectivityChangedEventBroadcasterBuilder().build();

        ConnectivityChangedEventProvider eventProvider
                = new ConnectivityChangedEventProvider();

        return new ConnectivityChangeReceiverProxy(broadcaster, eventProvider);
    }
}
