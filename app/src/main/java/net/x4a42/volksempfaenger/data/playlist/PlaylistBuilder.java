package net.x4a42.volksempfaenger.data.playlist;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.skippedepisode.SkippedEpisodeDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.skippedepisode.SkippedEpisodeDaoWrapper;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProviderBuilder;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

class PlaylistBuilder
{
    public Playlist build(Context context)
    {
        PlaylistItemDaoWrapper        playlistItemDao   = new PlaylistItemDaoBuilder().build(context);
        EpisodeDaoWrapper             episodeDao        = new EpisodeDaoBuilder().build(context);
        SkippedEpisodeDaoWrapper      skippedEpisodeDao = new SkippedEpisodeDaoBuilder().build(context);
        PlaybackServiceIntentProvider intentProvider    = new PlaybackServiceIntentProviderBuilder().build(context);

        SortedSet<Long> episodeIdSet = new TreeSet<>(new Comparator<Long>()
        {
            @Override
            public int compare(Long lhs, Long rhs)
            {
                return lhs.equals(rhs) ? 0 : lhs < rhs ? 1 : -1;
            }
        });

        return new Playlist(context,
                            playlistItemDao,
                            episodeDao,
                            skippedEpisodeDao,
                            intentProvider,
                            episodeIdSet);
    }
}
