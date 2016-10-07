package net.x4a42.volksempfaenger.service.syncall;

import android.content.Context;
import android.content.Intent;

import net.x4a42.volksempfaenger.IntentBuilder;

public class SyncAllServiceIntentProvider
{
    private final Context       context;
    private final IntentBuilder intentBuilder;

    public SyncAllServiceIntentProvider(Context       context,
                                        IntentBuilder intentBuilder)
    {
        this.context       = context;
        this.intentBuilder = intentBuilder;
    }

    public Intent getSyncIntent()
    {
        return intentBuilder.build(context, SyncAllService.class)
                .setAction(SyncAllService.ActionSync);
    }
}
