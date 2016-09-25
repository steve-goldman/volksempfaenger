package net.x4a42.volksempfaenger.data.entity.podcast;

import net.x4a42.volksempfaenger.data.entity.DaoWrapperBase;

import java.util.List;

public class PodcastDaoWrapper extends DaoWrapperBase<Podcast>
{
    private final PodcastProvider provider;

    public PodcastDaoWrapper(PodcastDao podcastDao,
                             PodcastProvider provider)
    {
        super(podcastDao);
        this.provider = provider;
    }

    public Podcast newPodcast(String feedUrl)
    {
        Podcast podcast = provider.get();
        podcast.setFeedUrl(feedUrl);
        return podcast;
    }

    public List<Podcast> getAll()
    {
        return dao.queryBuilder().listLazy();
    }

    public Podcast getById(long podcastId)
    {
        return dao.queryBuilder()
                .where(PodcastDao.Properties._id.eq(podcastId))
                .list().get(0);
    }

    public Podcast getByFeedUrl(String feedUrl)
    {
        List<Podcast> list = dao.queryBuilder()
                .where(PodcastDao.Properties.FeedUrl.eq(feedUrl))
                .list();

        if (list.isEmpty())
        {
            return null;
        }

        return list.get(0);
    }

}
