package net.x4a42.volksempfaenger.data.entity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoMaster;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDao;
import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDao;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDao;
import net.x4a42.volksempfaenger.data.entity.skippedepisode.SkippedEpisodeDao;

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

        if (oldVersion < 3)
        {
            PlaylistItemDao.createTable(wrap(db), true);
        }

        if (oldVersion < 4)
        {
            SkippedEpisodeDao.createTable(wrap(db), true);
        }

        if (oldVersion < 5)
        {
            EpisodeDownloadDao.createTable(wrap(db), true);
        }

        if (oldVersion < 6)
        {
            Log.d(this, "creating PODCAST_PUB_DATE index in EPISODE table");
            db.execSQL("CREATE INDEX PODCAST_PUB_DATE ON EPISODE (PODCAST_ID, PUB_DATE DESC)");
        }
    }
}
