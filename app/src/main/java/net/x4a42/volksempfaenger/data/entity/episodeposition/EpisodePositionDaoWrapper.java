package net.x4a42.volksempfaenger.data.entity.episodeposition;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;

import java.util.List;

public class EpisodePositionDaoWrapper
{
    private final EpisodePositionDao      dao;
    private final EpisodePositionProvider provider;

    public EpisodePositionDaoWrapper(EpisodePositionDao      dao,
                                     EpisodePositionProvider provider)
    {
        this.dao      = dao;
        this.provider = provider;
    }

    public EpisodePosition getOrInsert(Episode episode)
    {
        List<EpisodePosition> list = getListByEpisode(episode);

        if (list.isEmpty())
        {
            EpisodePosition episodePosition = provider.get();
            episodePosition.setEpisode(episode);
            episodePosition.setPosition(0);
            dao.insert(episodePosition);
            return episodePosition;
        }

        return list.get(0);
    }

    public void delete(EpisodePosition episodePosition)
    {
        dao.delete(episodePosition);
    }

    public void delete(Episode episode)
    {
        List<EpisodePosition> list = getListByEpisode(episode);
        if (!list.isEmpty())
        {
            delete(list.get(0));
        }
    }

    public void update(EpisodePosition episodePosition, int position)
    {
        episodePosition.setPosition(position);
        dao.update(episodePosition);
    }

    private List<EpisodePosition> getListByEpisode(Episode episode)
    {
        return dao.queryBuilder()
                  .where(EpisodePositionDao.Properties.EpisodeId.eq(episode.get_id()))
                  .list();
    }
}
