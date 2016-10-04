package net.x4a42.volksempfaenger.data.playlist;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItem;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.skippedepisode.SkippedEpisodeDaoWrapper;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.service.playlistdownload.PlaylistDownloadServiceIntentProvider;

import java.util.SortedSet;

public class Playlist
{
    private final Context                               context;
    private final PlaylistItemDaoWrapper                playlistItemDao;
    private final EpisodeDaoWrapper                     episodeDao;
    private final SkippedEpisodeDaoWrapper              skippedEpisodeDao;
    private final PlaybackServiceIntentProvider         playbackIntentProvider;
    private final SortedSet<Long>                       episodeIdSet;
    private final PlaylistDownloadServiceIntentProvider downloadIntentProvider;
    private boolean                                     isPlaying;

    Playlist(Context                               context,
             PlaylistItemDaoWrapper                playlistItemDao,
             EpisodeDaoWrapper                     episodeDao,
             SkippedEpisodeDaoWrapper              skippedEpisodeDao,
             PlaybackServiceIntentProvider         playbackIntentProvider,
             PlaylistDownloadServiceIntentProvider downloadIntentProvider,
             SortedSet<Long>                       episodeIdSet)
    {
        this.context                = context;
        this.playlistItemDao        = playlistItemDao;
        this.episodeDao             = episodeDao;
        this.skippedEpisodeDao      = skippedEpisodeDao;
        this.playbackIntentProvider = playbackIntentProvider;
        this.episodeIdSet           = episodeIdSet;
        this.downloadIntentProvider = downloadIntentProvider;
    }

    public void setPlaying(boolean isPlaying)
    {
        this.isPlaying  = isPlaying;
        unsetSkipped(getCurrentEpisode());
    }

    public boolean isPlaying()
    {
        return isPlaying;
    }

    public synchronized boolean isEmpty()
    {
        return playlistItemDao.getAll().isEmpty();
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
        startDownloadService();
    }

    public synchronized void episodeEnded()
    {
        setPlaying(false);
        PlaylistItem playlistItem = playlistItemDao.getByPosition(0);
        playlistItemDao.delete(playlistItem);
        startDownloadService();
    }

    public synchronized void episodeSkipped()
    {
        setPlaying(false);
        skippedEpisodeDao.create(getCurrentEpisode());

        PlaylistItem lastItem = null;
        for (PlaylistItem playlistItem : playlistItemDao.getAll())
        {
            lastItem        = playlistItem;
            Episode episode = playlistItem.getEpisode();
            if (!skippedEpisodeDao.hasEpisode(episode))
            {
                moveEpisode(episode, 0);
                return;
            }
        }

        if (lastItem != null)
        {
            moveEpisode(lastItem.getEpisode(), 0);
        }
    }

    public synchronized void removeItem(int position)
    {
        PlaylistItem playlistItem = playlistItemDao.getByPosition(position);
        unsetSkipped(playlistItem.getEpisode());
        playlistItemDao.delete(playlistItem);
        startDownloadService();
    }

    public synchronized void removeItem(long[] playlistItemIds)
    {
        for (long playlistItemId : playlistItemIds)
        {
            PlaylistItem playlistItem = playlistItemDao.getById(playlistItemId);
            unsetSkipped(playlistItem.getEpisode());
            playlistItemDao.delete(playlistItem);
        }
        startDownloadService();
    }

    public synchronized void moveItem(int fromPosition, int toPosition)
    {
        PlaylistItem playlistItem = playlistItemDao.getByPosition(fromPosition);
        unsetSkipped(playlistItem.getEpisode());
        playlistItemDao.move(playlistItem, toPosition);
        startDownloadService();
    }

    public synchronized boolean playEpisodeNow(Episode episode)
    {
        moveEpisode(episode, isPlaying ? 1 : 0);
        return startPlaybackService();
    }

    public synchronized boolean playEpisodesNow(long[] episodeIds)
    {
        populateEpisodeIdSet(episodeIds);
        for (long episodeId : episodeIdSet)
        {
            Episode episode = episodeDao.getById(episodeId);
            moveEpisode(episode, isPlaying ? 1 : 0);
        }
        return startPlaybackService();
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

    private void moveEpisode(Episode episode, int position)
    {
        PlaylistItem playlistItem = playlistItemDao.hasEpisode(episode) ?
                playlistItemDao.getByEpisode(episode) : playlistItemDao.createPlaylistItem(episode);

        playlistItemDao.move(playlistItem, position);
        startDownloadService();
    }

    private void startDownloadService()
    {
        context.startService(downloadIntentProvider.getRunIntent());
    }

    private boolean startPlaybackService()
    {
        if (!isPlaying)
        {
            context.startService(playbackIntentProvider.getPlayIntent());
            return true;
        }
        return false;
    }

    private void unsetSkipped(Episode episode)
    {
        if (skippedEpisodeDao.hasEpisode(episode))
        {
            skippedEpisodeDao.delete(episode);
        }
    }
}
