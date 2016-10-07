package net.x4a42.volksempfaenger.ui.episodelist;

import android.content.Context;

import net.x4a42.volksempfaenger.IntentBuilder;

public class EpisodeListActivityIntentProviderBuilder
{
    public EpisodeListActivityIntentProvider build(Context context)
    {
        IntentBuilder intentBuilder = new IntentBuilder();
        return new EpisodeListActivityIntentProvider(context, intentBuilder);
    }
}
