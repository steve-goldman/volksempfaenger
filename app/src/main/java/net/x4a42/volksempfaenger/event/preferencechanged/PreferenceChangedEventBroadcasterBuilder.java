package net.x4a42.volksempfaenger.event.preferencechanged;

import org.greenrobot.eventbus.EventBus;

public class PreferenceChangedEventBroadcasterBuilder
{
    public PreferenceChangedEventBroadcaster build()
    {
        return new PreferenceChangedEventBroadcaster(EventBus.getDefault());
    }
}
