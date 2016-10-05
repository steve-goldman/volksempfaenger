package net.x4a42.volksempfaenger.data.entity.skippedepisode;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.DaoSessionProvider;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoSession;

public class SkippedEpisodeDaoBuilder
{
    public SkippedEpisodeDaoWrapper build(Context context)
    {
        DaoSession             daoSession = new DaoSessionProvider(context).get();
        SkippedEpisodeDao      dao        = daoSession.getSkippedEpisodeDao();
        SkippedEpisodeProvider provider   = new SkippedEpisodeProvider();

        return new SkippedEpisodeDaoWrapper(dao, provider);
    }
}
