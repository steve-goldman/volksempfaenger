package net.x4a42.volksempfaenger.data.playlist;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;

class PlaylistBuilder
{
    public Playlist build(Context context)
    {
        PlaylistItemDaoWrapper playlistItemDao = new PlaylistItemDaoBuilder().build(context);

        return new Playlist(playlistItemDao);
    }
}
