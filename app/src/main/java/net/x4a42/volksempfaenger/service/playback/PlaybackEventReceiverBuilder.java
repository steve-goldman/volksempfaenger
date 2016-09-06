package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;

public class PlaybackEventReceiverBuilder
{
    public PlaybackEventReceiver build(Context context)
    {
        PlaybackEventActionMapper playbackEventActionMapper = new PlaybackEventActionMapper();

        return new PlaybackEventReceiver(context,
                                         playbackEventActionMapper);
    }
}
