package net.x4a42.volksempfaenger.ui.viewepisode;

import android.content.Context;

import net.x4a42.volksempfaenger.IntentBuilder;

public class ViewEpisodeActivityIntentProviderBuilder
{
    public ViewEpisodeActivityIntentProvider build(Context context)
    {
        return new ViewEpisodeActivityIntentProvider(context,
                                                     new IntentBuilder());
    }
}
