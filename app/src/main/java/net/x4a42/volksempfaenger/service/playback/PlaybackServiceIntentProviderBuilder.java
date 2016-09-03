package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;

public class PlaybackServiceIntentProviderBuilder
{
    public PlaybackServiceIntentProvider build(Context context)
    {
        PlaybackServiceIntentFactory intentFactory = new PlaybackServiceIntentFactory(context);
        return new PlaybackServiceIntentProvider(intentFactory);
    }
}
