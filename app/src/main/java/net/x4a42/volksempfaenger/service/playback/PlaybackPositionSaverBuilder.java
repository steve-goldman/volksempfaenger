package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;

import net.x4a42.volksempfaenger.misc.ContentValuesFactory;

class PlaybackPositionSaverBuilder
{
    public PlaybackPositionSaver build(Context context)
    {
        return new PlaybackPositionSaver(context.getContentResolver(), new ContentValuesFactory());
    }
}
