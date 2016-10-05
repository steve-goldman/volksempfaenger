package net.x4a42.volksempfaenger.event.playlistdownload;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;

public class EpisodeDownloadEventBuilder
{
    public EpisodeDownloadEvent build(Episode episode, EpisodeDownloadEvent.Action action)
    {
        return new EpisodeDownloadEvent(episode, action);
    }
}
