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
                          long    pubDate,
                          long    duration)
    {
        Episode episode = provider.get();
        episode.setPodcast(podcast);
        episode.setEpisodeUrl(episodeUrl);
        episode.setTitle(title);
        episode.setDescription(description);
        episode.setPubDate(pubDate);
        episode.setDuration(duration);
        dao.insert(episode);
        episode.getPodcast().resetEpisodes();
        return episode;
    }

    public void update(Episode episode,
                       String  title,
                       String  description,
                       long    pubDate,
                       long    duration)
    {
        episode.setTitle(title);
        episode.setDescription(description);
        episode.setPubDate(pubDate);
        episode.setDuration(duration);
        dao.update(episode);
    }

    public List<Episode> getAll(Podcast podcast)
    {
        return dao.queryBuilder()
                .where(EpisodeDao.Properties.PodcastId.eq(podcast.get_id()))
                .orderDesc(EpisodeDao.Properties.PubDate)
                .listLazy();
    }

    public void deleteAll(Podcast podcast)
    {
        for (Episode episode : getAll(podcast))
        {
            delete(episode);
        }
    }

    public Episode getById(long episodeId)
    {
        return dao.queryBuilder()
                .where(EpisodeDao.Properties._id.eq(episodeId))
                .list().get(0);
    }

    public Episode getByTitleAndDate(String title, long pubDate)
    {
        List<Episode> list = dao.queryBuilder()
                                .where(EpisodeDao.Properties.Title.eq(title),
                                       EpisodeDao.Properties.PubDate.eq(pubDate))
                                .list();

        if (list.isEmpty())
        {
            return null;
        }

        return list.get(0);
    }

    void delete(Episode episode)
    {
        dao.delete(episode);
    }
}
