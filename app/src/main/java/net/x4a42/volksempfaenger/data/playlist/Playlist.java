package net.x4a42.volksempfaenger.data.playlist;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItem;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;

public class Playlist
{
    private final PlaylistItemDaoWrapper playlistItemDao;

    Playlist(PlaylistItemDaoWrapper playlistItemDao)
    {
        this.playlistItemDao = playlistItemDao;
    }

    public synchronized Episode getCurrentEpisode()
    {
        return getHead().getEpisode();
    }

    public synchronized void addEpisode(Episode episode)
    {
        PlaylistItem playlistItem = playlistItemDao.createPlaylistItem(episode);
    }

    public synchronized void removeEpisode(Episode episode)
    {
        PlaylistItem playlistItem = playlistItemDao.getByEpisode(episode);
        playlistItemDao.delete(playlistItem);
    }

    public synchronized void moveEpisode(Episode episode, int newPosition)
    {
        PlaylistItem playlistItem = playlistItemDao.getByEpisode(episode);
        playlistItemDao.move(playlistItem, newPosition);
    }

    public synchronized void moveCurrentEpisodeToBack()
    {
        playlistItemDao.moveToBack(getHead());
    }

    public synchronized void moveEpisodeToCurrent(Episode episode)
    {
        PlaylistItem playlistItem = playlistItemDao.getByEpisode(episode);
        playlistItemDao.move(playlistItem, 0);
    }

    private PlaylistItem getHead()
    {
        return playlistItemDao.getByPosition(0);
    }

}
