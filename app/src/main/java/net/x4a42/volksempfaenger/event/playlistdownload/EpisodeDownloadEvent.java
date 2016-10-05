package net.x4a42.volksempfaenger.event.playlistdownload;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;

public class EpisodeDownloadEvent
{
    public enum Action
    {
        DOWNLOADED,
        FAILED,
        REMOVED
    }

    private final Episode episode;
    private final Action  action;

    public EpisodeDownloadEvent(Episode episode, Action  action)
    {
        this.episode = episode;
        this.action  = action;
    }

    public Episode getEpisode()
    {
        return episode;
    }

    public Action getAction()
    {
        return action;
    }
}
