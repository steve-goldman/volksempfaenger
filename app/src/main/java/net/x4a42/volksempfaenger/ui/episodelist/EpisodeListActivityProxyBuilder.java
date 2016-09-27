package net.x4a42.volksempfaenger.ui.episodelist;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;

public class EpisodeListActivityProxyBuilder
{
    public EpisodeListActivityProxy build(EpisodeListActivity activity)
    {
        IntentParser      intentParser = new IntentParser(activity.getIntent());
        PodcastDaoWrapper podcastDao   = new PodcastDaoBuilder().build(activity);
        Podcast           podcast      = podcastDao.getById(intentParser.getPodcastId());
        ListManager       listManager  = new ListManagerBuilder().build(activity, podcast);

        return new EpisodeListActivityProxy(activity, listManager);
    }
}
