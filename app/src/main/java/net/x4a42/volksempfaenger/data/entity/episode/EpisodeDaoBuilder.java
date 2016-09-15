package net.x4a42.volksempfaenger.data.entity.episode;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.DaoSessionBuilder;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoSession;

public class EpisodeDaoBuilder
{
    public EpisodeDaoWrapper build(Context context)
    {
        DaoSession      daoSession = new DaoSessionBuilder().build(context);
        EpisodeDao      episodeDao = daoSession.getEpisodeDao();
        EpisodeProvider provider   = new EpisodeProvider();

        return new EpisodeDaoWrapper(episodeDao, provider);
    }
}
