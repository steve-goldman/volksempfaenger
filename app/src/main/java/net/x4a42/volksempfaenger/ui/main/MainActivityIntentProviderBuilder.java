package net.x4a42.volksempfaenger.ui.main;

import android.content.Context;

import net.x4a42.volksempfaenger.IntentBuilder;

public class MainActivityIntentProviderBuilder
{
    public MainActivityIntentProvider build(Context context)
    {
        return new MainActivityIntentProvider(context, new IntentBuilder());
    }
}
