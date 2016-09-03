package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class PlaybackMediaButtonReceiverBuilder
{
    public PlaybackMediaButtonReceiver build(Context context)
    {
        PlaybackServiceIntentProvider intentProvider
                = new PlaybackServiceIntentProviderBuilder().build(context);

        PlaybackMediaButtonReceiver mediaButtonReceiver =
                new PlaybackMediaButtonReceiver(context, intentProvider);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        context.registerReceiver(mediaButtonReceiver, intentFilter);

        return mediaButtonReceiver;
    }
}
