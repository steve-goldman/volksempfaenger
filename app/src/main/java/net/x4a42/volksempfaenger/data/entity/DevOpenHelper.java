package net.x4a42.volksempfaenger.data.entity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.x4a42.volksempfaenger.data.entity.enclosure.DaoMaster;
import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDao;

class DevOpenHelper extends DaoMaster.DevOpenHelper
{
    public DevOpenHelper(Context context, String name)
    {
        super(context, name);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (oldVersion < 2)
        {
            EpisodePositionDao.createTable(wrap(db), true);
        }
    }
}
