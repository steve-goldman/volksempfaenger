package net.x4a42.volksempfaenger.data.entity.podcast;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PodcastDaoWrapperTest
{
    @Mock PodcastDao            podcastDao;
    @Mock PodcastProvider       provider;
    @Mock Podcast               podcast;
    @Mock QueryBuilder<Podcast> queryBuilder;
    List<Podcast>               matchingList = new ArrayList<>();
    List<Podcast>               emptyList    = new ArrayList<>();
    long                        podcastId    = 10;
    String                      url          = "this-is-my-url";
    PodcastDaoWrapper           daoWrapper;

    @Before
    public void setUp() throws Exception
    {
        matchingList.add(podcast);
        Mockito.when(provider.get()).thenReturn(podcast);
        Mockito.when(podcastDao.queryBuilder()).thenReturn(queryBuilder);
        Mockito.when(queryBuilder.where(Mockito.any(WhereCondition.class))).thenReturn(queryBuilder);
        daoWrapper = new PodcastDaoWrapper(podcastDao, provider);
    }

    @Test
    public void newPodcast() throws Exception
    {
        Podcast newPodcast = daoWrapper.newPodcast(url);
        assertEquals(podcast, newPodcast);
        Mockito.verify(podcast).setFeedUrl(url);
    }

    @Test
    public void getById() throws Exception
    {
        Mockito.when(queryBuilder.list()).thenReturn(matchingList);
        assertEquals(podcast, daoWrapper.getById(podcastId));
    }

    @Test
    public void getByFeedUrl() throws Exception
    {
        Mockito.when(queryBuilder.list()).thenReturn(matchingList);
        assertEquals(podcast, daoWrapper.getByFeedUrl(url));
    }

    @Test
    public void getByFeedUrlNoMatch() throws Exception
    {
        Mockito.when(queryBuilder.list()).thenReturn(emptyList);
        assertNull(daoWrapper.getByFeedUrl(url));
    }
}