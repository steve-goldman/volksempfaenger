package net.x4a42.volksempfaenger.service.playlistdownload;

import android.content.Context;

import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.PreferencesBuilder;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;

class PlaylistDownloadTaskProxyBuilder
{
    private final Context context;

    public PlaylistDownloadTaskProxyBuilder(Context context)
    {
        this.context = context;
    }

    public PlaylistDownloadTaskProxy build()
    {
        Preferences                   preferences        = new PreferencesBuilder().build(context);
        PlaylistItemDaoWrapper        playlistItemDao    = new PlaylistItemDaoBuilder().build(context);
        EpisodeDownloadDaoWrapper     episodeDownloadDao = new EpisodeDownloadDaoBuilder().build(context);

        return new PlaylistDownloadTaskProxy(preferences,
                                             playlistItemDao,
                                             episodeDownloadDao);
    }
}
