package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;

public class FeedSyncTaskBuilder
{
    public FeedSyncTask build(Context context)
    {
        return new FeedSyncTask(context);
    }
}
