package net.x4a42.volksempfaenger.data.entity.enclosure;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.DaoSessionBuilder;

public class EnclosureDaoBuilder
{
    public EnclosureDaoWrapper build(Context context)
    {
        DaoSession        daoSession = new DaoSessionBuilder().build(context);
        EnclosureDao      dao        = daoSession.getEnclosureDao();
        EnclosureProvider provider   = new EnclosureProvider();

        return new EnclosureDaoWrapper(dao, provider);
    }
}
