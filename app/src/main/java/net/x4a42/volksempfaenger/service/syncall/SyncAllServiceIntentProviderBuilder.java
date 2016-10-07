package net.x4a42.volksempfaenger.service.syncall;

import android.content.Context;

import net.x4a42.volksempfaenger.IntentBuilder;

public class SyncAllServiceIntentProviderBuilder
{
    public SyncAllServiceIntentProvider build(Context context)
    {
        IntentBuilder intentBuilder = new IntentBuilder();

        return new SyncAllServiceIntentProvider(context, intentBuilder);
    }
}
