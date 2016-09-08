package net.x4a42.volksempfaenger.service.playback;

import android.net.Uri;

public class PlaybackServiceFacade
{
    private Controller controller;

    public PlaybackServiceFacade(Controller controller)
    {
        this.controller = controller;
    }

    public Uri getEpisodeUri()
    {
        PlaybackItem playbackItem = controller.getPlaybackItem();
        return playbackItem != null ? playbackItem.getEpisodeUri() : null;
    }

    public boolean isPlaying()
    {
        return controller.isPlaying();
    }

    public boolean isOpen()
    {
        return controller.isOpen();
    }

    public int getDuration()
    {
        return controller.getDuration();
    }

    public int getPosition()
    {
        return controller.getPosition();
    }
}
