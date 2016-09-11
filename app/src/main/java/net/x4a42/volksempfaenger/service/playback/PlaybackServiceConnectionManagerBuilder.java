package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;

public class PlaybackServiceConnectionManagerBuilder
{
    public PlaybackServiceConnectionManager build(Context context)
    {
        PlaybackServiceIntentProvider intentProvider
                = new PlaybackServiceIntentProviderBuilder().build(context);

        return new PlaybackServiceConnectionManager(context, intentProvider);
    }
}
