package net.x4a42.volksempfaenger.data.entity.episode;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.enclosure.EnclosureDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.enclosure.EnclosureDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.skippedepisode.SkippedEpisodeDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.skippedepisode.SkippedEpisodeDaoWrapper;
import net.x4a42.volksempfaenger.data.playlist.Playlist;
import net.x4a42.volksempfaenger.data.playlist.PlaylistProvider;

public class EpisodeDeleterBuilder
{
    public EpisodeDeleter build(Context context)
    {
        EpisodeDaoWrapper         episodeDao         = new EpisodeDaoBuilder().build(context);
        EnclosureDaoWrapper       enclosureDao       = new EnclosureDaoBuilder().build(context);
        EpisodeDownloadDaoWrapper episodeDownloadDao = new EpisodeDownloadDaoBuilder().build(context);
        EpisodePositionDaoWrapper episodePositionDao = new EpisodePositionDaoBuilder().build(context);
        SkippedEpisodeDaoWrapper  skippedEpisodeDao  = new SkippedEpisodeDaoBuilder().build(context);
        Playlist                  playlist           = new PlaylistProvider(context).get();

        return new EpisodeDeleter(episodeDao,
                                  enclosureDao,
                                  episodeDownloadDao,
                                  episodePositionDao,
                                  skippedEpisodeDao,
                                  playlist);
    }
}
