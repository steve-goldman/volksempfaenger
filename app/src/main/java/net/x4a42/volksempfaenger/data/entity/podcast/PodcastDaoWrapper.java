package net.x4a42.volksempfaenger.data.entity.podcast;

import net.x4a42.volksempfaenger.misc.NowProvider;

import java.util.List;

public class PodcastDaoWrapper
{
    private final PodcastDao      dao;
    private final PodcastProvider provider;
    private final NowProvider     nowProvider;

    public PodcastDaoWrapper(PodcastDao      dao,
                             PodcastProvider provider,
                             NowProvider     nowProvider)
    {
        this.dao         = dao;
        this.provider    = provider;
        this.nowProvider = nowProvider;
    }

    public Podcast insert(String feedUrl)
    {
        Podcast podcast = provider.get();
        podcast.setFeedUrl(feedUrl);
        dao.insert(podcast);
        return podcast;
    }

    public void update(Podcast podcast,
                       String  title,
                       String  description,
                       String  website)
    {
        podcast.setTitle(title);
        podcast.setDescription(description);
        podcast.setWebsite(website);
        podcast.setLastUpdate(nowProvider.get());
        dao.update(podcast);
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
