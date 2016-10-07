package net.x4a42.volksempfaenger.ui.settings;

import android.content.Context;
import android.content.Intent;

import net.x4a42.volksempfaenger.IntentBuilder;

public class SettingsActivityIntentProvider
{
    private final Context       context;
    private final IntentBuilder intentBuilder;

    public SettingsActivityIntentProvider(Context       context,
                                          IntentBuilder intentBuilder)
    {
        this.context       = context;
        this.intentBuilder = intentBuilder;
    }

    public Intent get()
    {
        return intentBuilder.build(context, SettingsActivity.class);
    }
}
