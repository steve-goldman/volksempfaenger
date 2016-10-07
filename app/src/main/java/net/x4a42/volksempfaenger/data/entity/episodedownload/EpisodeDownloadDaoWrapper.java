package net.x4a42.volksempfaenger.data.entity.episodedownload;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.downloadmanager.DownloadManagerAdapter;

import java.util.List;

public class EpisodeDownloadDaoWrapper
{
    private final EpisodeDownloadDao      dao;
    private final EpisodeDownloadProvider provider;
    private final DownloadManagerAdapter  downloadManagerAdapter;


    public EpisodeDownloadDaoWrapper(EpisodeDownloadDao      dao,
                                     EpisodeDownloadProvider provider,
                                     DownloadManagerAdapter  downloadManagerAdapter)
    {
        this.dao                    = dao;
        this.provider               = provider;
        this.downloadManagerAdapter = downloadManagerAdapter;
    }

    public List<EpisodeDownload> getAll()
    {
        return dao.queryBuilder().listLazy();
    }

    public boolean hasEpisode(Episode episode)
    {
        return !getEpisodeList(episode).isEmpty();
    }

    public boolean hasSuccessfulDownload(Episode episode)
    {
        if (!hasEpisode(episode))
        {
            return false;
        }

        EpisodeDownload episodeDownload = getByEpisode(episode);
        return downloadManagerAdapter.isSuccess(episodeDownload.getDownloadId());
    }

    private EpisodeDownload getByEpisode(Episode episode)
    {
        return getEpisodeList(episode).get(0);
    }

    public boolean hasDownloadId(long downloadId)
    {
        return !getDownloadIdList(downloadId).isEmpty();
    }

    public EpisodeDownload getByDownloadId(long downloadId)
    {
        return getDownloadIdList(downloadId).get(0);
    }

    public void delete(EpisodeDownload episodeDownload)
    {
        dao.delete(episodeDownload);
    }

    public EpisodeDownload insert(Episode episode, long downloadId)
    {
        EpisodeDownload episodeDownload = provider.get();
        episodeDownload.setEpisode(episode);
        episodeDownload.setDownloadId(downloadId);
        dao.insert(episodeDownload);
        return episodeDownload;
    }

    private List<EpisodeDownload> getEpisodeList(Episode episode)
    {
        return dao.queryBuilder()
                  .where(EpisodeDownloadDao.Properties.EpisodeId.eq(episode.get_id()))
                  .list();
    }

    private List<EpisodeDownload> getDownloadIdList(long downloadId)
    {
        return dao.queryBuilder()
                  .where(EpisodeDownloadDao.Properties.DownloadId.eq(downloadId))
                  .list();
    }
}
