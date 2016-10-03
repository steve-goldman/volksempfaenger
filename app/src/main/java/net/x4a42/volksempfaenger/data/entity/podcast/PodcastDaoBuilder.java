package net.x4a42.volksempfaenger.data.entity.podcast;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.DaoSessionBuilder;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoSession;
import net.x4a42.volksempfaenger.misc.NowProvider;

public class PodcastDaoBuilder
{
    public PodcastDaoWrapper build(Context context)
    {
        DaoSession      daoSession  = new DaoSessionBuilder().build(context);
        PodcastDao      dao         = daoSession.getPodcastDao();
        PodcastProvider provider    = new PodcastProvider();
        NowProvider     nowProvider = new NowProvider();

        return new PodcastDaoWrapper(dao, provider, nowProvider);
    }
}
