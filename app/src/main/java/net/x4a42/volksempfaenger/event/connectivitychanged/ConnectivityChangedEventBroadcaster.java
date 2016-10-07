package net.x4a42.volksempfaenger.event.connectivitychanged;

import org.greenrobot.eventbus.EventBus;

public class ConnectivityChangedEventBroadcaster
{
    private final EventBus eventBus;

    public ConnectivityChangedEventBroadcaster(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }

    public void broadcast(ConnectivityChangedEvent event)
    {
        eventBus.post(event);
    }
}
