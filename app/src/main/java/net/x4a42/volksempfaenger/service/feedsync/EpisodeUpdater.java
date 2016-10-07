package net.x4a42.volksempfaenger.service.feedsync;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.playlist.Playlist;
import net.x4a42.volksempfaenger.feedparser.FeedItem;

class EpisodeUpdater
{
    private final EpisodeDaoWrapper      episodeDao;
    private final Playlist               playlist;
    private final EnclosureUpdater       enclosureUpdater;

    public EpisodeUpdater(EpisodeDaoWrapper      episodeDao,
                          Playlist               playlist,
                          EnclosureUpdater       enclosureUpdater)
    {
        this.episodeDao             = episodeDao;
        this.playlist               = playlist;
        this.enclosureUpdater       = enclosureUpdater;
    }

    public void insertOrUpdate(Podcast podcast, FeedItem feedItem, boolean firstSync)
    {
        Episode episode = insertOrUpdateEpisode(podcast, feedItem, firstSync);
        insertOrUpdateEnclosures(episode, feedItem);
    }

    private Episode insertOrUpdateEpisode(Podcast podcast, FeedItem feedItem, boolean firstSync)
    {
        Episode episode = episodeDao.getByUrl(feedItem.getUrl());
        if (episode == null)
        {
            return insertEpisode(podcast, feedItem, firstSync);
        }
        else
        {
            updateEpisode(episode, feedItem);
            return episode;
        }
    }

    private Episode insertEpisode(Podcast podcast, FeedItem feedItem, boolean firstSync)
    {
        boolean anyPodcastEpisodes = !podcast.getEpisodes().isEmpty();
        Episode episode            = episodeDao.insert(podcast,
                                                       feedItem.getUrl(),
                                                       feedItem.title,
                                                       feedItem.description,
                                                       feedItem.date.getTime(),
                                                       feedItem.duration);

        if (!firstSync || !anyPodcastEpisodes)
        {
            playlist.addEpisode(episode);
        }

        return episode;
    }

    private void updateEpisode(Episode episode, FeedItem feedItem)
    {
        episodeDao.update(episode,
                          feedItem.title,
                          feedItem.description,
                          feedItem.date.getTime(),
                          feedItem.duration);
    }

    private void insertOrUpdateEnclosures(Episode episode, FeedItem feedItem)
    {
        for (net.x4a42.volksempfaenger.feedparser.Enclosure feedEnclosure : feedItem.enclosures)
        {
            enclosureUpdater.insertOrUpdate(episode, feedEnclosure);
        }
    }
}
