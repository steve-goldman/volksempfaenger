package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Intent;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;

class IntentParser
{
    public interface Listener
    {
        void onSync(Podcast podcast);
    }

    private final PodcastDaoWrapper podcastDao;
    private Listener                listener;

    public IntentParser(PodcastDaoWrapper podcastDao)
    {
        this.podcastDao = podcastDao;
    }

    public IntentParser setListener(Listener listener)
    {
        this.listener = listener;
        return this;
    }

    public void parse(Intent intent)
    {
        if (listener == null)
        {
            return;
        }

        if (intent == null)
        {
            return;
        }

        String action = intent.getAction();
        if (action == null)
        {
            return;
        }

        switch (action)
        {
            case FeedSyncService.ActionSync:
                handleSync(intent);
                break;
            default:
                Log.e(this, String.format("unexpected action:%s", action));
                break;
        }
    }

    private void handleSync(Intent intent)
    {
        long    podcastId = intent.getLongExtra(FeedSyncServiceIntentProvider.PodcastIdKey, -1);
        Podcast podcast   = podcastDao.getById(podcastId);
        listener.onSync(podcast);
    }
}
