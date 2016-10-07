package net.x4a42.volksempfaenger.service.syncall;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;
import net.x4a42.volksempfaenger.service.feedsync.FeedSyncServiceIntentProvider;

class SyncAllServiceProxy implements IntentParser.Listener
{
    private final Context                       context;
    private final IntentParser                  intentParser;
    private final PodcastDaoWrapper             podcastDao;
    private final FeedSyncServiceIntentProvider intentProvider;

    public SyncAllServiceProxy(Context                       context,
                               IntentParser                  intentParser,
                               PodcastDaoWrapper             podcastDao,
                               FeedSyncServiceIntentProvider intentProvider)
    {
        this.context        = context;
        this.intentParser   = intentParser;
        this.podcastDao     = podcastDao;
        this.intentProvider = intentProvider;
    }

    public int onStartCommand(Intent intent)
    {
        intentParser.parse(intent);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onSync()
    {
        for (Podcast podcast : podcastDao.getAll())
        {
            context.startService(intentProvider.getSyncIntent(podcast));
        }
    }
}
