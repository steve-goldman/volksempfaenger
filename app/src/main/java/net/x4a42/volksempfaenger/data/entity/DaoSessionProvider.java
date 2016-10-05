package net.x4a42.volksempfaenger.data.entity;

import android.content.Context;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoMaster;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoSession;

import org.greenrobot.greendao.database.Database;

public class DaoSessionProvider
{
    private final Context                  context;
    private static DaoMaster.DevOpenHelper openHelper;
    private static DaoSession              instance;

    public DaoSessionProvider(Context context)
    {
        this.context = context;
    }

    public DaoSession get()
    {
        if (instance == null)
        {
            DaoMaster.DevOpenHelper helper = getHelper(context);
            Database                db     = helper.getWritableDb();
            instance = new DaoMaster(db).newSession();
        }

        return instance;
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
