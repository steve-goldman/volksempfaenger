package net.x4a42.volksempfaenger.data.entity;

import org.greenrobot.greendao.AbstractDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DaoWrapperBaseTest
{
    @Mock AbstractDao<Long, Long> dao;
    long                          entity = 10;
    DaoWrapperBase<Long>          daoWrapper;

    @Before
    public void setUp() throws Exception
    {
        daoWrapper = new DaoWrapperBase<>(dao);
    }

    @Test
    public void insert() throws Exception
    {
        daoWrapper.insert(entity);
        Mockito.verify(dao).insert(entity);
    }

    @Test
    public void update() throws Exception
    {
        daoWrapper.update(entity);
        Mockito.verify(dao).update(entity);
    }
}