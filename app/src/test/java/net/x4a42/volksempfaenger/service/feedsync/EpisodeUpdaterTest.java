package net.x4a42.volksempfaenger.service.feedsync;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItem;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.playlist.Playlist;
import net.x4a42.volksempfaenger.feedparser.Enclosure;
import net.x4a42.volksempfaenger.feedparser.FeedItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class EpisodeUpdaterTest
{
    @Mock EpisodeDaoWrapper      episodeDao;
    @Mock Playlist               playlist;
    @Mock EnclosureUpdater       enclosureUpdater;
    @Mock Episode                episode;
    @Mock Podcast                podcast;
    @Mock List<Episode>          episodes;
    @Mock PlaylistItem           playlistItem;
    FeedItem                     feedItem       = new FeedItem();
    Enclosure                    feedEnclosure1 = new Enclosure();
    Enclosure                    feedEnclosure2 = new Enclosure();
    EpisodeUpdater               episodeUpdater;

    @Before
    public void setUp() throws Exception
    {
        feedItem.title       = "my-title";
        feedItem.description = "my-description";
        feedItem.url         = "my-url";
        feedItem.date        = new Date();
        feedItem.enclosures.add(feedEnclosure1);
        feedItem.enclosures.add(feedEnclosure2);
        Mockito.when(episodeDao.insert(podcast,
                                       feedItem.url,
                                       feedItem.title,
                                       feedItem.description,
                                       feedItem.date.getTime()))
               .thenReturn(episode);
        Mockito.when(podcast.getEpisodes()).thenReturn(episodes);
        feedItem.date        = new Date();
        episodeUpdater       = new EpisodeUpdater(episodeDao, playlist, enclosureUpdater);
    }

    @Test
    public void insertNotFirstSync() throws Exception
    {
        episodeUpdater.insertOrUpdate(podcast, feedItem, false);
        Mockito.verify(episodeDao).insert(podcast,
                                          feedItem.url,
                                          feedItem.title,
                                          feedItem.description,
                                          feedItem.date.getTime());
        Mockito.verify(playlist).addEpisode(episode);
    }

    @Test
    public void insertFirstSyncEmpty() throws Exception
    {
        Mockito.when(episodes.isEmpty()).thenReturn(true);
        episodeUpdater.insertOrUpdate(podcast, feedItem, true);
        Mockito.verify(episodeDao).insert(podcast,
                                          feedItem.url,
                                          feedItem.title,
                                          feedItem.description,
                                          feedItem.date.getTime());
        Mockito.verify(playlist).addEpisode(episode);
    }

    @Test
    public void insertFirstSyncNotEmpty() throws Exception
    {
        episodeUpdater.insertOrUpdate(podcast, feedItem, true);
        Mockito.verify(episodeDao).insert(podcast,
                                          feedItem.url,
                                          feedItem.title,
                                          feedItem.description,
                                          feedItem.date.getTime());
        Mockito.verify(playlist, Mockito.never()).addEpisode(episode);
    }

    @Test
    public void update() throws Exception
    {
        Mockito.when(episodeDao.getByUrl(feedItem.url)).thenReturn(episode);
        episodeUpdater.insertOrUpdate(podcast, feedItem, false);
        Mockito.verify(episodeDao).update(episode,
                                          feedItem.title,
                                          feedItem.description,
                                          feedItem.date.getTime());
    }
}