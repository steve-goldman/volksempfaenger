package net.x4a42.volksempfaenger.data.entity.skippedepisode;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.DaoSessionBuilder;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoSession;

public class SkippedEpisodeDaoBuilder
{
    public SkippedEpisodeDaoWrapper build(Context context)
    {
        DaoSession             daoSession        = new DaoSessionBuilder().build(context);
        SkippedEpisodeDao      skippedEpisodeDao = daoSession.getSkippedEpisodeDao();
        SkippedEpisodeProvider provider          = new SkippedEpisodeProvider();

        return new SkippedEpisodeDaoWrapper(skippedEpisodeDao, provider);
    }
}
