package net.x4a42.volksempfaenger.data.entity.enclosure;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class EnclosureDaoWrapperTest
{
    @Mock EnclosureDao            enclosureDao;
    @Mock EnclosureProvider       provider;
    @Mock Enclosure               enclosure;
    @Mock Episode                 episode;
    @Mock QueryBuilder<Enclosure> queryBuilder;
    List<Enclosure>               matchingList = new ArrayList<>();
    List<Enclosure>               emptyList    = new ArrayList<>();
    String                        url          = "this-is-my-url";
    EnclosureDaoWrapper           daoWrapper;

    @Before
    public void setUp() throws Exception
    {
        matchingList.add(enclosure);
        Mockito.when(provider.get()).thenReturn(enclosure);
        Mockito.when(enclosureDao.queryBuilder()).thenReturn(queryBuilder);
        Mockito.when(queryBuilder.where(Mockito.any(WhereCondition.class))).thenReturn(queryBuilder);
        daoWrapper = new EnclosureDaoWrapper(enclosureDao, provider);
    }

    @Test
    public void newEnclosure() throws Exception
    {
        Enclosure newEnclosure = daoWrapper.newEnclosure(episode, url);
        assertEquals(enclosure, newEnclosure);
        Mockito.verify(enclosure).setEpisode(episode);
        Mockito.verify(enclosure).setUrl(url);
    }

    @Test
    public void getByUrl() throws Exception
    {
        Mockito.when(queryBuilder.list()).thenReturn(matchingList);
        assertEquals(enclosure, daoWrapper.getByUrl(url));
    }

    @Test
    public void getByUrlNoMatch() throws Exception
    {
        Mockito.when(queryBuilder.list()).thenReturn(emptyList);
        assertNull(daoWrapper.getByUrl(url));
    }
}