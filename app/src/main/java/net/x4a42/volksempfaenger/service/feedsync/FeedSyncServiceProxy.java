package net.x4a42.volksempfaenger.service.feedsync;

import android.app.Service;
import android.content.Intent;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;

class FeedSyncServiceProxy implements IntentParser.Listener
{
    private final IntentParser         intentParser;
    private final FeedSyncTaskProvider taskProvider;

    public FeedSyncServiceProxy(IntentParser         intentParser,
                                FeedSyncTaskProvider taskProvider)
    {
        this.intentParser = intentParser;
        this.taskProvider = taskProvider;
    }

    public int onStartCommand(Intent intent)
    {
        intentParser.parse(intent);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onSync(Podcast podcast)
    {
        taskProvider.get().execute(podcast);
    }
}
