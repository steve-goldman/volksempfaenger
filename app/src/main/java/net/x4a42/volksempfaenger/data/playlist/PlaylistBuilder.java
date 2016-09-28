package net.x4a42.volksempfaenger.data.playlist;

import android.content.Context;

import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.PreferencesBuilder;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;

class PlaylistBuilder
{
    public Playlist build(Context context)
    {
        PlaylistItemDaoWrapper playlistItemDao = new PlaylistItemDaoBuilder().build(context);
        Preferences            preferences     = new PreferencesBuilder().build(context);

        return new Playlist(playlistItemDao, preferences);
    }
}
