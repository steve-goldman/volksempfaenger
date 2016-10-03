package net.x4a42.volksempfaenger.service.feedsync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;

class FeedSyncServiceProxy implements IntentParser.Listener
{
    private final Context             context;
    private final IntentParser        intentParser;
    private final FeedSyncTaskBuilder taskBuilder;

    public FeedSyncServiceProxy(Context             context,
                                IntentParser        intentParser,
                                FeedSyncTaskBuilder taskBuilder)
    {
        this.context      = context;
        this.intentParser = intentParser;
        this.taskBuilder  = taskBuilder;
    }

    public void onCreate()
    {

    }

    public void onDestroy()
    {

    }

    public int onStartCommand(Intent intent)
    {
        intentParser.parse(intent);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onSync(Podcast podcast)
    {
        taskBuilder.build(context).execute(podcast);
    }
}
