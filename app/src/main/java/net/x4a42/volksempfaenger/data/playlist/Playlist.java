package net.x4a42.volksempfaenger.data.playlist;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItem;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;

import java.util.SortedSet;

public class Playlist
{
    private final PlaylistItemDaoWrapper playlistItemDao;
    private final EpisodeDaoWrapper      episodeDao;
    private final SortedSet<Long>        episodeIdSet;

    Playlist(PlaylistItemDaoWrapper playlistItemDao,
             EpisodeDaoWrapper      episodeDao,
             SortedSet<Long>        episodeIdSet)
    {
        this.playlistItemDao = playlistItemDao;
        this.episodeDao      = episodeDao;
        this.episodeIdSet    = episodeIdSet;
    }

    public synchronized Episode getCurrentEpisode()
    {
        return getHead().getEpisode();
    }

    public synchronized Episode getEpisode(int position)
    {
        PlaylistItem playlistItem = playlistItemDao.getByPosition(position);
        return playlistItem.getEpisode();
    }

    public synchronized void addEpisode(Episode episode)
    {
        playlistItemDao.createPlaylistItem(episode);
    }

    public synchronized void removeItem(int position)
    {
        PlaylistItem playlistItem = playlistItemDao.getByPosition(position);
        playlistItemDao.delete(playlistItem);
    }

    public synchronized void removeItem(long[] playlistItemIds)
    {
        for (long playlistItemId : playlistItemIds)
        {
            PlaylistItem playlistItem = playlistItemDao.getById(playlistItemId);
            playlistItemDao.delete(playlistItem);
        }
    }

    public synchronized void moveItem(int fromPosition, int toPosition)
    {
        PlaylistItem playlistItem = playlistItemDao.getByPosition(fromPosition);
        playlistItemDao.move(playlistItem, toPosition);
    }

    public synchronized void moveCurrentItemToBack()
    {
        playlistItemDao.moveToBack(getHead());
    }

    public synchronized void moveEpisodeToHead(Episode episode)
    {
        PlaylistItem playlistItem = playlistItemDao.getByEpisode(episode);
        playlistItemDao.move(playlistItem, 0);
    }

    public synchronized void playEpisodesNext(long[] episodeIds)
    {
        populateEpisodeIdSet(episodeIds);
        for (long episodeId : episodeIdSet)
        {
            Episode episode = episodeDao.getById(episodeId);
            if (!playlistItemDao.hasEpisode(episode))
            {
                playlistItemDao.createPlaylistItem(episode);
            }
            moveEpisodeToHead(episode);
        }
    }

    private PlaylistItem getHead()
    {
        return playlistItemDao.getByPosition(0);
    }

    private void populateEpisodeIdSet(long[] episodeIds)
    {
        episodeIdSet.clear();
        for (long episodeId : episodeIds)
        {
            episodeIdSet.add(episodeId);
        }
    }
}
