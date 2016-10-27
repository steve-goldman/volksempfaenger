package net.x4a42.volksempfaenger.data.entity.podcast;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDeleter;

public class PodcastDeleter
{
    private final PodcastDaoWrapper podcastDao;
    private final EpisodeDaoWrapper episodeDao;
    private final EpisodeDeleter    episodeDeleter;

    public PodcastDeleter(PodcastDaoWrapper podcastDao,
                          EpisodeDaoWrapper episodeDao,
                          EpisodeDeleter    episodeDeleter)
    {
        this.podcastDao     = podcastDao;
        this.episodeDao     = episodeDao;
        this.episodeDeleter = episodeDeleter;
    }

    public void delete(Podcast podcast)
    {
        for (Episode episode : episodeDao.getAll(podcast))
        {
            episodeDeleter.delete(episode);
        }

        podcastDao.delete(podcast);
    }
}
