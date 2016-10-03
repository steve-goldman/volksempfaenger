package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;

class FeedSyncTaskProvider
{
    private final Context                  context;
    private final FeedSyncTaskProxyBuilder taskProxyBuilder;

    public FeedSyncTaskProvider(Context                  context,
                                FeedSyncTaskProxyBuilder taskProxyBuilder)
    {
        this.context          = context;
        this.taskProxyBuilder = taskProxyBuilder;
    }

    public FeedSyncTask get()
    {
        return new FeedSyncTask(taskProxyBuilder);
    }
}
