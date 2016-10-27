package net.x4a42.volksempfaenger.data.entity.podcast;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDeleter;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDeleterBuilder;

public class PodcastDeleterBuilder
{
    public PodcastDeleter build(Context context)
    {
        PodcastDaoWrapper podcastDao     = new PodcastDaoBuilder().build(context);
        EpisodeDaoWrapper episodeDao     = new EpisodeDaoBuilder().build(context);
        EpisodeDeleter    episodeDeleter = new EpisodeDeleterBuilder().build(context);

        return new PodcastDeleter(podcastDao, episodeDao, episodeDeleter);
    }
}
