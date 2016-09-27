package net.x4a42.volksempfaenger.data.entity.episodeposition;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.DaoSessionBuilder;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoSession;

public class EpisodePositionDaoBuilder
{
    public EpisodePositionDaoWrapper build(Context context)
    {
        DaoSession              daoSession         = new DaoSessionBuilder().build(context);
        EpisodePositionDao      episodePositionDao = daoSession.getEpisodePositionDao();
        EpisodePositionProvider provider           = new EpisodePositionProvider();

        return new EpisodePositionDaoWrapper(episodePositionDao, provider);
    }
}
