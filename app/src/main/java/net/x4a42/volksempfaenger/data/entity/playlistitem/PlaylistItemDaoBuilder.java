package net.x4a42.volksempfaenger.data.entity.playlistitem;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.DaoSessionBuilder;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoSession;

public class PlaylistItemDaoBuilder
{
    public PlaylistItemDaoWrapper build(Context context)
    {
        DaoSession           daoSession = new DaoSessionBuilder().build(context);
        PlaylistItemDao      dao        = daoSession.getPlaylistItemDao();
        PlaylistItemProvider provider   = new PlaylistItemProvider();

        return new PlaylistItemDaoWrapper(dao, provider);
    }
}
