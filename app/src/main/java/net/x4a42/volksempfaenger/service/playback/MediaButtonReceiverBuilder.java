package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

class MediaButtonReceiverBuilder
{
    public MediaButtonReceiver build(Context context)
    {
        PlaybackServiceIntentProvider intentProvider
                = new PlaybackServiceIntentProviderBuilder().build(context);

        MediaButtonReceiver mediaButtonReceiver =
                new MediaButtonReceiver(context, intentProvider);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        context.registerReceiver(mediaButtonReceiver, intentFilter);

        return mediaButtonReceiver;
    }
}
