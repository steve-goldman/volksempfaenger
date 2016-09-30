package net.x4a42.volksempfaenger.data.entity.skippedepisode;

import net.x4a42.volksempfaenger.data.entity.DaoWrapperBase;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;

import java.util.List;

public class SkippedEpisodeDaoWrapper extends DaoWrapperBase<SkippedEpisode>
{
    private final SkippedEpisodeProvider provider;

    public SkippedEpisodeDaoWrapper(SkippedEpisodeDao      skippedEpisodeDao,
                                    SkippedEpisodeProvider provider)
    {
        super(skippedEpisodeDao);
        this.provider = provider;
    }

    public boolean hasEpisode(Episode episode)
    {
        return !getForEpisode(episode).isEmpty();
    }

    public void delete(Episode episode)
    {
        SkippedEpisode skippedEpisode = getForEpisode(episode).get(0);
        delete(skippedEpisode);
    }

    public SkippedEpisode create(Episode episode)
    {
        SkippedEpisode skippedEpisode = provider.get();
        skippedEpisode.setEpisode(episode);
        insert(skippedEpisode);
        return skippedEpisode;
    }

    public List<SkippedEpisode> getForEpisode(Episode episode)
    {
        return dao.queryBuilder()
                  .where(SkippedEpisodeDao.Properties.EpisodeId.eq(episode.get_id()))
                  .list();
    }
}
