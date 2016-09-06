package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.os.Handler;

public class PlaybackBackgroundPositionSaverBuilder
{
    public PlaybackBackgroundPositionSaver build(Context context)
    {
        PlaybackPositionSaver saver = new PlaybackPositionSaverBuilder().build(context);
        return new PlaybackBackgroundPositionSaver(saver, new Handler());
    }
}
