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
    String                      title        = "this-is-my-title";
    long                        pubDate      = 200;
    EpisodeDaoWrapper           daoWrapper;

    @Before
    public void setUp() throws Exception
    {
        matchingList.add(episode);
        Mockito.when(provider.get()).thenReturn(episode);
        Mockito.when(episodeDao.queryBuilder()).thenReturn(queryBuilder);
        Mockito.when(queryBuilder.where(Mockito.any(WhereCondition.class),
                                        Mockito.any(WhereCondition.class))).thenReturn(queryBuilder);
        daoWrapper = new EpisodeDaoWrapper(episodeDao, provider);
    }

    @Test
    public void getByTitleAndDate() throws Exception
    {
        Mockito.when(queryBuilder.list()).thenReturn(matchingList);
        assertEquals(episode, daoWrapper.getByTitleAndDate(title, pubDate));
    }

    @Test
    public void getByTitleAndDateNoMatch() throws Exception
    {
        Mockito.when(queryBuilder.list()).thenReturn(emptyList);
        assertNull(daoWrapper.getByTitleAndDate(title, pubDate));
    }
}