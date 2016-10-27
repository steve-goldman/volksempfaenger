package net.x4a42.volksempfaenger.data.entity.episode;

import net.x4a42.volksempfaenger.data.entity.enclosure.Enclosure;
import net.x4a42.volksempfaenger.data.entity.enclosure.EnclosureDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.skippedepisode.SkippedEpisodeDaoWrapper;
import net.x4a42.volksempfaenger.data.playlist.Playlist;

public class EpisodeDeleter
{
    private final EpisodeDaoWrapper         episodeDao;
    private final EnclosureDaoWrapper       enclosureDao;
    private final EpisodeDownloadDaoWrapper episodeDownloadDao;
    private final EpisodePositionDaoWrapper episodePositionDao;
    private final SkippedEpisodeDaoWrapper  skippedEpisodeDao;
    private final Playlist                  playlist;

    public EpisodeDeleter(EpisodeDaoWrapper             episodeDao,
                          EnclosureDaoWrapper           enclosureDao,
                          EpisodeDownloadDaoWrapper     episodeDownloadDao,
                          EpisodePositionDaoWrapper     episodePositionDao,
                          SkippedEpisodeDaoWrapper      skippedEpisodeDao,
                          Playlist                      playlist)
    {
        this.episodeDao         = episodeDao;
        this.enclosureDao       = enclosureDao;
        this.episodeDownloadDao = episodeDownloadDao;
        this.episodePositionDao = episodePositionDao;
        this.skippedEpisodeDao  = skippedEpisodeDao;
        this.playlist           = playlist;
    }

    public void delete(Episode episode)
    {
        for (Enclosure enclosure : episode.getEnclosures())
        {
            enclosureDao.delete(enclosure);
        }

        if (playlist.isPlaying() && episode.get_id().equals(playlist.getCurrentEpisode().get_id()))
        {
            playlist.pause();
        }
        playlist.removeEpisode(episode);
        episodeDownloadDao.delete(episode);
        episodePositionDao.delete(episode);
        skippedEpisodeDao.delete(episode);

        episodeDao.delete(episode);
    }
}
