package net.x4a42.volksempfaenger.data.entity.episode;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoWrapper;

public class EpisodePathResolver
{
    private final EpisodePathProvider       pathProvider;
    private final EpisodeDownloadDaoWrapper episodeDownloadDao;

    public EpisodePathResolver(EpisodePathProvider       pathProvider,
                               EpisodeDownloadDaoWrapper episodeDownloadDao)
    {
        this.pathProvider           = pathProvider;
        this.episodeDownloadDao     = episodeDownloadDao;
    }

    public String resolveUrl(Episode episode)
    {
        if (episodeDownloadDao.hasSuccessfulDownload(episode))
        {
            Log.d(this, "playing from local file");
            return pathProvider.getEpisodeUrl(episode);
        }

        Log.d(this, "playing from web url");
        return episode.getEnclosures().get(0).getUrl();
    }
}
