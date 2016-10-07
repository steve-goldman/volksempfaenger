package net.x4a42.volksempfaenger.ui.settings;

import android.content.Context;

import net.x4a42.volksempfaenger.IntentBuilder;

public class SettingsActivityIntentProviderBuilder
{
    public SettingsActivityIntentProvider build(Context context)
    {
        return new SettingsActivityIntentProvider(context, new IntentBuilder());
    }
}
