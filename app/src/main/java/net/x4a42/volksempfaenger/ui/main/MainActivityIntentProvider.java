package net.x4a42.volksempfaenger.ui.main;

import android.content.Context;
import android.content.Intent;

import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.ui.MainActivity;

public class MainActivityIntentProvider
{
    private final Context       context;
    private final IntentBuilder intentBuilder;

    public MainActivityIntentProvider(Context       context,
                                      IntentBuilder intentBuilder)
    {
        this.context       = context;
        this.intentBuilder = intentBuilder;
    }

    public Intent getIntent()
    {
        return intentBuilder.build(context, MainActivity.class);
    }
}
