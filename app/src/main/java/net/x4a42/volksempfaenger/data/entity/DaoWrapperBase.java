package net.x4a42.volksempfaenger.data.entity;

import org.greenrobot.greendao.AbstractDao;

public class DaoWrapperBase<T>
{
    protected final AbstractDao<T, Long> dao;

    public DaoWrapperBase(AbstractDao<T, Long> dao)
    {
        this.dao = dao;
    }

    public long insert(T entity)
    {
        return dao.insert(entity);
    }

    public void update(T entity)
    {
        dao.update(entity);
    }

    public void delete(T entity)
    {
        dao.delete(entity);
    }

    public void insertOrReplace(T entity)
    {
        dao.insertOrReplace(entity);
    }
}
