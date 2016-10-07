package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.media.session.MediaSession;

import net.x4a42.volksempfaenger.event.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventReceiverBuilder;

class MediaSessionManagerBuilder
{
    public MediaSessionManager build(Context context)
    {
        MediaSession          mediaSession = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
        {
            mediaSession = new MediaSession(context, getClass().getName());
        }

        PlaybackEventReceiver eventReceiver = new PlaybackEventReceiverBuilder().build();

        MediaSessionManager   mediaSessionManager
                = new MediaSessionManager(mediaSession, eventReceiver);

        eventReceiver.setListener(mediaSessionManager);

        return mediaSessionManager;
    }
}
