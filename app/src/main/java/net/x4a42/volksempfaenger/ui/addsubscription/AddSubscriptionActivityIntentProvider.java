package net.x4a42.volksempfaenger.ui.addsubscription;

import android.content.Context;
import android.content.Intent;

import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.ui.AddSubscriptionActivity;

public class AddSubscriptionActivityIntentProvider
{
    private final Context       context;
    private final IntentBuilder intentBuilder;

    public AddSubscriptionActivityIntentProvider(Context       context,
                                                 IntentBuilder intentBuilder)
    {
        this.context       = context;
        this.intentBuilder = intentBuilder;
    }

    public Intent get()
    {
        return intentBuilder.build(context, AddSubscriptionActivity.class);
    }
}
