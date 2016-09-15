package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;

import net.x4a42.volksempfaenger.IntentBuilder;

public class FeedSyncServiceIntentProviderBuilder
{
    public FeedSyncServiceIntentProvider build(Context context)
    {
        IntentBuilder intentBuilder = new IntentBuilder();

        return new FeedSyncServiceIntentProvider(context, intentBuilder);
    }
}
