package net.x4a42.volksempfaenger.service.playback;

import android.support.annotation.Nullable;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;

public class PlaybackServiceFacade
{
    private Controller controller;

    public PlaybackServiceFacade(Controller controller)
    {
        this.controller = controller;
    }

    public boolean isEpisodeOpen(Episode episode)
    {
        Episode playbackEpisode = controller.getPlaybackEpisode();
        return playbackEpisode != null && playbackEpisode.get_id().equals(episode.get_id());
    }

    @Nullable
    public Episode getEpisode()
    {
        return controller.getPlaybackEpisode();
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
