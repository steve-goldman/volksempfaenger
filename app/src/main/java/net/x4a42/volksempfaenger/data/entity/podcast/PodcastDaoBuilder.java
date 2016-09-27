package net.x4a42.volksempfaenger.data.entity.podcast;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.DaoSessionBuilder;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoSession;

public class PodcastDaoBuilder
{
    public PodcastDaoWrapper build(Context context)
    {
        DaoSession      daoSession = new DaoSessionBuilder().build(context);
        PodcastDao      podcastDao = daoSession.getPodcastDao();
        PodcastProvider provider   = new PodcastProvider();

        return new PodcastDaoWrapper(podcastDao, provider);
    }
}
