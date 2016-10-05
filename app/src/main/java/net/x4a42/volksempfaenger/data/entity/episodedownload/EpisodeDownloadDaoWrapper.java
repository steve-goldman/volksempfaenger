package net.x4a42.volksempfaenger.data.entity.episodedownload;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;

import java.util.List;

public class EpisodeDownloadDaoWrapper
{
    private final EpisodeDownloadDao      dao;
    private final EpisodeDownloadProvider provider;

    public EpisodeDownloadDaoWrapper(EpisodeDownloadDao      dao,
                                     EpisodeDownloadProvider provider)
    {
        this.dao      = dao;
        this.provider = provider;
    }

    public List<EpisodeDownload> getAll()
    {
        return dao.queryBuilder().listLazy();
    }

    public boolean hasEpisode(Episode episode)
    {
        return !getEpisodeList(episode).isEmpty();
    }

    public EpisodeDownload getByEpisode(Episode episode)
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
