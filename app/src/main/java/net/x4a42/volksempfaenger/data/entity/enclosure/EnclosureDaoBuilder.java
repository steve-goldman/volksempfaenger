package net.x4a42.volksempfaenger.data.entity.enclosure;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.DaoSessionProvider;

public class EnclosureDaoBuilder
{
    public EnclosureDaoWrapper build(Context context)
    {
        DaoSession        daoSession = new DaoSessionProvider(context).get();
        EnclosureDao      dao        = daoSession.getEnclosureDao();
        EnclosureProvider provider   = new EnclosureProvider();

        return new EnclosureDaoWrapper(dao, provider);
    }
}
