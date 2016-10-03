package net.x4a42.volksempfaenger.data.entity.episode;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;

import java.util.List;

public class EpisodeDaoWrapper
{
    private final EpisodeDao      dao;
    private final EpisodeProvider provider;

    public EpisodeDaoWrapper(EpisodeDao      dao,
                             EpisodeProvider provider)
    {
        this.dao      = dao;
        this.provider = provider;
    }

    public Episode insert(Podcast podcast,
                          String  episodeUrl,
                          String  title,
                          String  description,
                          long    pubDate)
    {
        Episode episode = provider.get();
        episode.setPodcast(podcast);
        episode.setEpisodeUrl(episodeUrl);
        episode.setTitle(title);
        episode.setDescription(description);
        episode.setPubDate(pubDate);
        dao.insert(episode);
        episode.getPodcast().resetEpisodes();
        return episode;
    }

    public void update(Episode episode,
                       String  title,
                       String  description,
                       long    pubDate)
    {
        episode.setTitle(title);
        episode.setDescription(description);
        episode.setPubDate(pubDate);
        dao.update(episode);
    }

    public List<Episode> getAll(Podcast podcast)
    {
        return dao.queryBuilder()
                .where(EpisodeDao.Properties.PodcastId.eq(podcast.get_id()))
                .listLazy();
    }

    public Episode getById(long episodeId)
    {
        return dao.queryBuilder()
                .where(EpisodeDao.Properties._id.eq(episodeId))
                .list().get(0);
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
