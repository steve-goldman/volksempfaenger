package net.x4a42.volksempfaenger.service.playback;

import android.os.Binder;

public class PlaybackServiceBinder extends Binder
{
    private final PlaybackServiceFacade playbackServiceFacade;

    public PlaybackServiceBinder(PlaybackServiceFacade playbackServiceFacade)
    {
        this.playbackServiceFacade = playbackServiceFacade;
    }

    public PlaybackServiceFacade getFacade()
    {
        return playbackServiceFacade;
    }
}
