package net.x4a42.volksempfaenger.data.entity.playlistitem;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;

import java.util.List;

public class PlaylistItemDaoWrapper
{
    private final PlaylistItemDao      dao;
    private final PlaylistItemProvider provider;

    public PlaylistItemDaoWrapper(PlaylistItemDao      dao,
                                  PlaylistItemProvider provider)
    {
        this.dao      = dao;
        this.provider = provider;
    }

    public PlaylistItem createPlaylistItem(Episode episode)
    {
        PlaylistItem playlistItem = provider.get();
        playlistItem.setEpisode(episode);
        playlistItem.setPosition(dao.count());
        dao.insert(playlistItem);
        return playlistItem;
    }

    public long count()
    {
        return dao.queryBuilder().count();
    }

    public List<PlaylistItem> getAll()
    {
        return dao.queryBuilder()
                  .orderAsc(PlaylistItemDao.Properties.Position)
                  .listLazy();
    }

    public PlaylistItem getByPosition(long position)
    {
        return dao.queryBuilder()
                  .where(PlaylistItemDao.Properties.Position.eq(position))
                  .list().get(0);
    }

    public boolean hasEpisode(Episode episode)
    {
        return !getListByEpisodeId(episode.get_id()).isEmpty();
    }

    public PlaylistItem getByEpisode(Episode episode)
    {
        return getListByEpisodeId(episode.get_id()).get(0);
    }

    public PlaylistItem getById(long playlistItemId)
    {
        return dao.queryBuilder()
                  .where(PlaylistItemDao.Properties._id.eq(playlistItemId))
                  .list().get(0);
    }

    public void delete(PlaylistItem playlistItem)
    {
        long deletedPosition = playlistItem.getPosition();
        dao.delete(playlistItem);

        for (PlaylistItem behindItem : getBehind(deletedPosition))
        {
            updatePosition(behindItem, behindItem.getPosition() - 1);
        }
    }

    public void move(PlaylistItem playlistItem, long newPosition)
    {
        if (newPosition == playlistItem.getPosition())
        {
            return;
        }

        long oldPosition = playlistItem.getPosition();
        // TODO: wrap in transaction?
        updatePosition(playlistItem, -1);
        if (newPosition < oldPosition)
        {
            for (PlaylistItem rangeItem : getRange(oldPosition - 1, newPosition))
            {
                updatePosition(rangeItem, rangeItem.getPosition() + 1);
            }
        }
        else
        {
            for (PlaylistItem rangeItem : getRange(oldPosition+ 1, newPosition))
            {
                updatePosition(rangeItem, rangeItem.getPosition() - 1);
            }
        }
        updatePosition(playlistItem, newPosition);
    }

    public List<PlaylistItem> getFirstN(int n)
    {
        return getRange(0, n - 1);
    }

    private void updatePosition(PlaylistItem playlistItem, long newPosition)
    {
        playlistItem.setPosition(newPosition);
        dao.update(playlistItem);
    }

    private List<PlaylistItem> getRange(long from, long to)
    {
        if (to == -1)
        {
            return dao.queryBuilder()
                      .where(PlaylistItemDao.Properties.Position.ge(from))
                      .orderAsc(PlaylistItemDao.Properties.Position)
                      .listLazy();
        }
        else if (from == -1)
        {
            return dao.queryBuilder()
                    .where(PlaylistItemDao.Properties.Position.le(to))
                    .orderDesc(PlaylistItemDao.Properties.Position)
                    .listLazy();
        }
        if (from < to)
        {
            return dao.queryBuilder()
                      .where(PlaylistItemDao.Properties.Position.ge(from),
                             PlaylistItemDao.Properties.Position.le(to))
                      .orderAsc(PlaylistItemDao.Properties.Position)
                      .listLazy();
        }
        else
        {
            return dao.queryBuilder()
                      .where(PlaylistItemDao.Properties.Position.ge(to),
                             PlaylistItemDao.Properties.Position.le(from))
                      .orderDesc(PlaylistItemDao.Properties.Position)
                      .listLazy();
        }
    }

    private List<PlaylistItem> getBehind(long position)
    {
        return getRange(position + 1, -1);
    }

    private List<PlaylistItem> getListByEpisodeId(long episodeId)
    {
        return dao.queryBuilder()
                  .where(PlaylistItemDao.Properties.EpisodeId.eq(episodeId))
                  .list();
    }

}
