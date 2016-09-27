package net.x4a42.volksempfaenger.data.entity.episode;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;

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
public class EpisodeDaoWrapperTest
{
    @Mock EpisodeDao            episodeDao;
    @Mock EpisodeProvider       provider;
    @Mock Episode               episode;
    @Mock Podcast               podcast;
    @Mock QueryBuilder<Episode> queryBuilder;
    List<Episode>               matchingList = new ArrayList<>();
    List<Episode>               emptyList    = new ArrayList<>();
    String                      url          = "this-is-my-url";
    EpisodeDaoWrapper           daoWrapper;

    @Before
    public void setUp() throws Exception
    {
        matchingList.add(episode);
        Mockito.when(provider.get()).thenReturn(episode);
        Mockito.when(episodeDao.queryBuilder()).thenReturn(queryBuilder);
        Mockito.when(queryBuilder.where(Mockito.any(WhereCondition.class))).thenReturn(queryBuilder);
        daoWrapper = new EpisodeDaoWrapper(episodeDao, provider);
    }

    @Test
    public void newEpisode() throws Exception
    {
        Episode newEpisode = daoWrapper.newEpisode(podcast, url);
        assertEquals(episode, newEpisode);
        Mockito.verify(episode).setPodcast(podcast);
        Mockito.verify(episode).setEpisodeUrl(url);
    }

    @Test
    public void getByUrl() throws Exception
    {
        Mockito.when(queryBuilder.list()).thenReturn(matchingList);
        assertEquals(episode, daoWrapper.getByUrl(url));
    }

    @Test
    public void getByUrlNoMatch() throws Exception
    {
        Mockito.when(queryBuilder.list()).thenReturn(emptyList);
        assertNull(daoWrapper.getByUrl(url));
    }
}