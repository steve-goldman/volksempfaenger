package net.x4a42.volksempfaenger.preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PreferenceChangedEventReceiver
{
    private final EventBus                 eventBus;
    private PreferenceChangedEventListener listener;

    public PreferenceChangedEventReceiver(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }

    public PreferenceChangedEventReceiver setListener(PreferenceChangedEventListener listener)
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
    public void onEvent(PreferenceChangedEvent event)
    {
        listener.onPreferenceChanged();
    }
}
