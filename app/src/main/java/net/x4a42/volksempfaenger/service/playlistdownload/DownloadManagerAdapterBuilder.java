package net.x4a42.volksempfaenger.service.playlistdownload;

import android.app.DownloadManager;
import android.content.Context;

import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.PreferencesBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodePathProvider;

class DownloadManagerAdapterBuilder
{
    public DownloadManagerAdapter build(Context context)
    {
        DownloadManager downloadManager
                = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManagerRequestBuilder requestBuilder = new DownloadManagerRequestBuilder();
        DownloadManagerQueryProvider  queryProvider  = new DownloadManagerQueryProvider();
        EpisodePathProvider           pathProvider   = new EpisodePathProvider(context);
        UriBuilder                    uriBuilder     = new UriBuilder();
        Preferences                   preferences    = new PreferencesBuilder().build(context);

        return new DownloadManagerAdapter(downloadManager,
                                          requestBuilder,
                                          queryProvider,
                                          pathProvider,
                                          uriBuilder,
                                          preferences);
    }
}
