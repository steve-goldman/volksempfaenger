package net.x4a42.volksempfaenger.data.entity.episodedownload;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.DaoSessionProvider;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoSession;

public class EpisodeDownloadDaoBuilder
{
    public EpisodeDownloadDaoWrapper build(Context context)
    {
        DaoSession              daoSession = new DaoSessionProvider(context).get();
        EpisodeDownloadDao      dao        = daoSession.getEpisodeDownloadDao();
        EpisodeDownloadProvider provider   = new EpisodeDownloadProvider();

        return new EpisodeDownloadDaoWrapper(dao, provider);
    }
}
