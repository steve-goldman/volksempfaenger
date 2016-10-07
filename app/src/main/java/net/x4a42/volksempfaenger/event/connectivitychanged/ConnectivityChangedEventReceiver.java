package net.x4a42.volksempfaenger.event.connectivitychanged;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ConnectivityChangedEventReceiver
{
    private final EventBus eventBus;
    private ConnectivityChangedEventListener listener;

    public ConnectivityChangedEventReceiver(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }

    public ConnectivityChangedEventReceiver setListener(ConnectivityChangedEventListener listener)
    {
        this.listener = listener;
        return this;
    }

    public void subscribe()
    {
        eventBus.register(this);
    }

    public void unsubscribe()
    {
        eventBus.unregister(this);
    }

    @Subscribe
    public void onEvent(ConnectivityChangedEvent event)
    {
        listener.onConnectivityChanged();
    }
}
