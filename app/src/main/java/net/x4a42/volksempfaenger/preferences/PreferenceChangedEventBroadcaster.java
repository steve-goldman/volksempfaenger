package net.x4a42.volksempfaenger.preferences;

import org.greenrobot.eventbus.EventBus;

public class PreferenceChangedEventBroadcaster
{
    private final EventBus eventBus;

    public PreferenceChangedEventBroadcaster(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }

    public void broadcast(PreferenceChangedEvent event)
    {
        eventBus.post(event);
    }
}
