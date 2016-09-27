package net.x4a42.volksempfaenger.data.entity.episodeposition;

import net.x4a42.volksempfaenger.data.entity.DaoWrapperBase;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;

import java.util.List;

public class EpisodePositionDaoWrapper extends DaoWrapperBase<EpisodePosition>
{
    private final EpisodePositionProvider provider;

    public EpisodePositionDaoWrapper(EpisodePositionDao      episodePositionDao,
                                     EpisodePositionProvider provider)
    {
        super(episodePositionDao);
        this.provider = provider;
    }

    public EpisodePosition getOrCreate(Episode episode)
    {
        List<EpisodePosition> list = getListByEpisode(episode);

        if (list.isEmpty())
        {
            EpisodePosition episodePosition = provider.get();
            episodePosition.setEpisode(episode);
            episodePosition.setPosition(0);
            return episodePosition;
        }

        return list.get(0);
    }

    private List<EpisodePosition> getListByEpisode(Episode episode)
    {
        return dao.queryBuilder()
                  .where(EpisodePositionDao.Properties.EpisodeId.eq(episode.get_id()))
                  .list();
    }
}
