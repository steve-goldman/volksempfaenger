package net.x4a42.volksempfaenger.service.playback;

import android.content.Intent;

/*
    Use this class if you want to create an intent to send to the PlaybackService
    to make it play, pause, or stop.
 */

public class PlaybackServiceIntentProvider
{
    public static final String                 PositionKey  = "position";
    public static final String                 OffsetKey    = "offset";
    private final PlaybackServiceIntentFactory intentFactory;

    public PlaybackServiceIntentProvider(PlaybackServiceIntentFactory intentFactory)
    {
        this.intentFactory = intentFactory;
    }

    public Intent getPlayIntent()
    {
        return intentFactory.create(PlaybackService.ActionPlay);
    }

    public Intent getPauseIntent()
    {
        return intentFactory.create(PlaybackService.ActionPause);
    }

    public Intent getPlayPauseIntent()
    {
        return intentFactory.create(PlaybackService.ActionPlayPause);
    }

    public Intent getStopIntent()
    {
        return intentFactory.create(PlaybackService.ActionStop);
    }

    public Intent getSeekIntent(int position)
    {
        return intentFactory.create(PlaybackService.ActionSeek).putExtra(PositionKey, position);
    }

    public Intent getMoveIntent(int offset)
    {
        return intentFactory.create(PlaybackService.ActionMove).putExtra(OffsetKey, offset);
    }

    public Intent getBindIntent()
    {
        return intentFactory.create();
    }
}
