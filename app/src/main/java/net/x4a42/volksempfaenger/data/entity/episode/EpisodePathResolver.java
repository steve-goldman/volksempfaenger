package net.x4a42.volksempfaenger.data.entity.episode;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownload;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoWrapper;
import net.x4a42.volksempfaenger.downloadmanager.DownloadManagerAdapter;

public class EpisodePathResolver
{
    private final EpisodePathProvider       pathProvider;
    private final EpisodeDownloadDaoWrapper episodeDownloadDao;
    private final DownloadManagerAdapter    downloadManagerAdapter;

    public EpisodePathResolver(EpisodePathProvider       pathProvider,
                               EpisodeDownloadDaoWrapper episodeDownloadDao,
                               DownloadManagerAdapter    downloadManagerAdapter)
    {
        this.pathProvider           = pathProvider;
        this.episodeDownloadDao     = episodeDownloadDao;
        this.downloadManagerAdapter = downloadManagerAdapter;
    }

    public String resolveUrl(Episode episode)
    {
        if (episodeDownloadDao.hasEpisode(episode))
        {
            EpisodeDownload episodeDownload = episodeDownloadDao.getByEpisode(episode);
            long            downloadId      = episodeDownload.getDownloadId();

            if (downloadManagerAdapter.isSuccess(downloadId))
            {
                Log.d(this, "playing from local file");
                return pathProvider.getEpisodeUrl(episode);
            }
        }

        Log.d(this, "playing from web url");
        return episode.getEnclosures().get(0).getUrl();
    }
}
