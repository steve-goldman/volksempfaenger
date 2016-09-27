package net.x4a42.volksempfaenger.data.entity;

import android.content.Context;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoMaster;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoSession;

import org.greenrobot.greendao.database.Database;

public class DaoSessionBuilder
{
    private static DaoMaster.DevOpenHelper openHelper;

    public DaoSession build(Context context)
    {
        DaoMaster.DevOpenHelper helper = getHelper(context);
        Database                db     = helper.getWritableDb();
        return new DaoMaster(db).newSession();
    }

    private synchronized DaoMaster.DevOpenHelper getHelper(Context context)
    {
        if (openHelper == null)
        {
            String dbName = context.getResources().getString(R.string.database_name);
            openHelper    = new DevOpenHelper(context, dbName);
        }

        return openHelper;
    }
}
