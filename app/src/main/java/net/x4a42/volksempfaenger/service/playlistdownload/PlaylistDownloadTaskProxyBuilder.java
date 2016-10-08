package net.x4a42.volksempfaenger.service.playlistdownload;

import android.content.Context;

import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.PreferencesBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;
import net.x4a42.volksempfaenger.downloadmanager.DownloadManagerAdapter;
import net.x4a42.volksempfaenger.downloadmanager.DownloadManagerAdapterBuilder;
import net.x4a42.volksempfaenger.event.episodedownload.EpisodeDownloadEventBroadcaster;
import net.x4a42.volksempfaenger.event.episodedownload.EpisodeDownloadEventBroadcasterBuilder;
import net.x4a42.volksempfaenger.misc.ConnectivityStatus;
import net.x4a42.volksempfaenger.misc.ConnectivityStatusBuilder;

import java.util.ArrayList;
import java.util.Collection;

class PlaylistDownloadTaskProxyBuilder
{
    private final Context context;

    public PlaylistDownloadTaskProxyBuilder(Context context)
    {
        this.context = context;
    }

    public PlaylistDownloadTaskProxy build()
    {
        Preferences                     preferences            = new PreferencesBuilder().build(context);
        PlaylistItemDaoWrapper          playlistItemDao        = new PlaylistItemDaoBuilder().build(context);
        EpisodeDownloadDaoWrapper       episodeDownloadDao     = new EpisodeDownloadDaoBuilder().build(context);
        DownloadManagerAdapter          downloadManagerAdapter = new DownloadManagerAdapterBuilder().build(context);
        ConnectivityStatus              connectivityStatus     = new ConnectivityStatusBuilder().build(context);
        EpisodeDownloadEventBroadcaster eventBroadcaster       = new EpisodeDownloadEventBroadcasterBuilder().build(context);
        Collection<Episode>             removedEpisodes        = new ArrayList<>();

        return new PlaylistDownloadTaskProxy(preferences,
                                             playlistItemDao,
                                             episodeDownloadDao,
                                             downloadManagerAdapter,
                                             connectivityStatus,
                                             removedEpisodes,
                                             eventBroadcaster);
    }
}
