package net.x4a42.volksempfaenger.data.entity.episode;

import net.x4a42.volksempfaenger.data.entity.DaoWrapperBase;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;

import java.util.List;

public class EpisodeDaoWrapper extends DaoWrapperBase<Episode>
{
    private final EpisodeProvider provider;

    public EpisodeDaoWrapper(EpisodeDao episodeDao,
                             EpisodeProvider provider)
    {
        super(episodeDao);
        this.provider = provider;
    }

    public Episode newEpisode(Podcast podcast, String episodeUrl)
    {
        Episode episode = provider.get();
        episode.setPodcast(podcast);
        episode.setEpisodeUrl(episodeUrl);
        return episode;
    }

    public Episode getByUrl(String episodeUrl)
    {
        List<Episode> list = dao.queryBuilder()
                                .where(EpisodeDao.Properties.EpisodeUrl.eq(episodeUrl))
                                .list();

        if (list.isEmpty())
        {
            return null;
        }

        return list.get(0);
    }
}
