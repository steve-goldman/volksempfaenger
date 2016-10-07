package net.x4a42.volksempfaenger.service.feedsync;

import android.os.AsyncTask;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;

import java.io.IOException;

class FeedSyncTask extends AsyncTask<Podcast, Void, Void>
{
    private final FeedSyncTaskProxyBuilder taskProxyBuilder;

    public FeedSyncTask(FeedSyncTaskProxyBuilder taskProxyBuilder)
    {
        this.taskProxyBuilder = taskProxyBuilder;
    }

    @Override
    protected Void doInBackground(Podcast... params)
    {
        try
        {
            taskProxyBuilder.build(params[0]).doInBackground();
        }
        catch (IOException e)
        {
            // TODO
            Log.e(this, e.getMessage());
        }
        return null;
    }
}
