package net.x4a42.volksempfaenger.ui.addsubscription;

import android.content.Context;

import net.x4a42.volksempfaenger.IntentBuilder;

public class AddSubscriptionActivityIntentProviderBuilder
{
    public AddSubscriptionActivityIntentProvider build(Context context)
    {
        IntentBuilder intentBuilder = new IntentBuilder();

        return new AddSubscriptionActivityIntentProvider(context,
                                                         intentBuilder);
    }
}
