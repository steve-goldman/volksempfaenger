package net.x4a42.volksempfaenger.data.entity.episodedownload;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.DaoSessionProvider;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoSession;
import net.x4a42.volksempfaenger.downloadmanager.DownloadManagerAdapter;
import net.x4a42.volksempfaenger.downloadmanager.DownloadManagerAdapterBuilder;

public class EpisodeDownloadDaoBuilder
{
    public EpisodeDownloadDaoWrapper build(Context context)
    {
        DaoSession              daoSession             = new DaoSessionProvider(context).get();
        EpisodeDownloadDao      dao                    = daoSession.getEpisodeDownloadDao();
        EpisodeDownloadProvider provider               = new EpisodeDownloadProvider();
        DownloadManagerAdapter  downloadManagerAdapter = new DownloadManagerAdapterBuilder().build(context);

        return new EpisodeDownloadDaoWrapper(dao, provider, downloadManagerAdapter);
    }
}
