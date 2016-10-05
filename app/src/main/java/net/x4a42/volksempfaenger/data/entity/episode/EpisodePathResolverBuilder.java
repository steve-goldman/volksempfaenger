package net.x4a42.volksempfaenger.data.entity.episode;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoWrapper;
import net.x4a42.volksempfaenger.downloadmanager.DownloadManagerAdapter;
import net.x4a42.volksempfaenger.downloadmanager.DownloadManagerAdapterBuilder;

public class EpisodePathResolverBuilder
{
    public EpisodePathResolver build(Context context)
    {
        EpisodePathProvider       pathProvider           = new EpisodePathProvider(context);
        EpisodeDownloadDaoWrapper episodeDownloadDao     = new EpisodeDownloadDaoBuilder().build(context);
        DownloadManagerAdapter    downloadManagerAdapter = new DownloadManagerAdapterBuilder().build(context);

        return new EpisodePathResolver(pathProvider, episodeDownloadDao, downloadManagerAdapter);
    }
}
