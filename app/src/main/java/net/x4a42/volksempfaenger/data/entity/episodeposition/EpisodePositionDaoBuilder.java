package net.x4a42.volksempfaenger.data.entity.episodeposition;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.DaoSessionProvider;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoSession;

public class EpisodePositionDaoBuilder
{
    public EpisodePositionDaoWrapper build(Context context)
    {
        DaoSession              daoSession = new DaoSessionProvider(context).get();
        EpisodePositionDao      dao        = daoSession.getEpisodePositionDao();
        EpisodePositionProvider provider   = new EpisodePositionProvider();

        return new EpisodePositionDaoWrapper(dao, provider);
    }
}
