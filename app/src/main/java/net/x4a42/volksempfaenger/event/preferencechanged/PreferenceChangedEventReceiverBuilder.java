package net.x4a42.volksempfaenger.event.preferencechanged;

import org.greenrobot.eventbus.EventBus;

public class PreferenceChangedEventReceiverBuilder
{
    public PreferenceChangedEventReceiver build()
    {
        return new PreferenceChangedEventReceiver(EventBus.getDefault());
    }
}
