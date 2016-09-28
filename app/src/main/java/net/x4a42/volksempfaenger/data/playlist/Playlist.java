package net.x4a42.volksempfaenger.data.playlist;

import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItem;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;

public class Playlist
{
    private final PlaylistItemDaoWrapper playlistItemDao;
    private final Preferences            preferences;

    Playlist(PlaylistItemDaoWrapper playlistItemDao,
             Preferences            preferences)
    {
        this.playlistItemDao = playlistItemDao;
        this.preferences     = preferences;
    }

    public synchronized Episode getCurrentEpisode()
    {
        PlaylistItem playlistItem = playlistItemDao.getByPosition(getCurrentPosition());
        return playlistItem.getEpisode();
    }

    public synchronized void addEpisode(Episode episode)
    {
        PlaylistItem playlistItem = playlistItemDao.newPlaylistItem(episode);
        playlistItemDao.insert(playlistItem);
        updateCurrentPosition(playlistItem.getEpisode());
    }

    public synchronized void removeEpisode(Episode episode)
    {
        PlaylistItem playlistItem = playlistItemDao.getByEpisode(episode);
        playlistItemDao.delete(playlistItem);
        updateCurrentPosition(playlistItem.getEpisode());
    }

    public synchronized void moveEpisode(Episode episode, int newPosition)
    {
        PlaylistItem playlistItem = playlistItemDao.getByEpisode(episode);
        playlistItemDao.move(playlistItem, newPosition);
        updateCurrentPosition(playlistItem.getEpisode());
    }

    public synchronized void moveCurrentEpisodeToBack()
    {
        PlaylistItem playlistItem = playlistItemDao.getByPosition(getCurrentPosition());
        playlistItemDao.moveToBack(playlistItem);
        updateCurrentPosition(playlistItem.getEpisode());
    }

    public synchronized void moveEpisodeToCurrent(Episode episode)
    {
        PlaylistItem playlistItem = playlistItemDao.getByEpisode(episode);
        playlistItemDao.move(playlistItem, getCurrentPosition());
        updateCurrentPosition(playlistItem.getEpisode());
    }

    public synchronized void advanceEpisode()
    {
        setCurrentPosition(getCurrentPosition() + 1);
    }

    private long getCurrentPosition()
    {
        return preferences.getPlaylistCurrentPosition();
    }

    private void setCurrentPosition(long newPosition)
    {
        preferences.setPlaylistCurrentPosition(newPosition);
    }

    private void updateCurrentPosition(Episode currentEpisode)
    {
        PlaylistItem playlistItem = playlistItemDao.getByEpisode(currentEpisode);
        setCurrentPosition(playlistItem.getPosition());
    }
}
