package net.x4a42.volksempfaenger.service.feedsync;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItem;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.feedparser.Enclosure;
import net.x4a42.volksempfaenger.feedparser.FeedItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class EpisodeUpdaterTest
{
    @Mock EpisodeDaoWrapper      episodeDao;
    @Mock PlaylistItemDaoWrapper playlistItemDao;
    @Mock EnclosureUpdater       enclosureUpdater;
    @Mock Episode                episode;
    @Mock Podcast                podcast;
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
        feedItem.enclosures.add(feedEnclosure1);
        feedItem.enclosures.add(feedEnclosure2);
        Mockito.when(episodeDao.newEpisode(podcast, feedItem.url)).thenReturn(episode);
        Mockito.when(playlistItemDao.createPlaylistItem(episode)).thenReturn(playlistItem);
        feedItem.date        = new Date();
        episodeUpdater       = new EpisodeUpdater(episodeDao, playlistItemDao, enclosureUpdater);
    }

    @Test
    public void insert() throws Exception
    {
        episodeUpdater.insertOrUpdate(podcast, feedItem);
        verifyCommon();
        Mockito.verify(episodeDao).insert(episode);
        Mockito.verify(playlistItemDao).insert(playlistItem);
    }

    @Test
    public void update() throws Exception
    {
        Mockito.when(episodeDao.getByUrl(feedItem.url)).thenReturn(episode);
        episodeUpdater.insertOrUpdate(podcast, feedItem);
        verifyCommon();
        Mockito.verify(episodeDao).update(episode);
    }

    private void verifyCommon() throws Exception
    {
        Mockito.verify(episode).setTitle(feedItem.title);
        Mockito.verify(episode).setDescription(feedItem.description);
        Mockito.verify(episode).setPubDate(feedItem.date.getTime());
        Mockito.verify(enclosureUpdater).insertOrUpdate(episode, feedEnclosure1);
        Mockito.verify(enclosureUpdater).insertOrUpdate(episode, feedEnclosure2);
    }
}