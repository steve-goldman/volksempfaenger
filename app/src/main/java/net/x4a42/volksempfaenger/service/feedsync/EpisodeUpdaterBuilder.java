package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;
import net.x4a42.volksempfaenger.data.playlist.Playlist;
import net.x4a42.volksempfaenger.data.playlist.PlaylistProvider;

class EpisodeUpdaterBuilder
{
    public EpisodeUpdater build(Context context)
    {
        EpisodeDaoWrapper      episodeDao       = new EpisodeDaoBuilder().build(context);
        Playlist               playlist         = new PlaylistProvider(context).get();
        EnclosureUpdater       enclosureUpdater = new EnclosureUpdaterBuilder().build(context);

        return new EpisodeUpdater(episodeDao, playlist, enclosureUpdater);
    }
}
