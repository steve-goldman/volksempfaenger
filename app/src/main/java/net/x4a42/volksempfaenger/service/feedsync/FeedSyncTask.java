package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;
import android.os.AsyncTask;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;

import java.io.IOException;

public class FeedSyncTask extends AsyncTask<Podcast, Void, Void>
{
    private final Context context;

    public FeedSyncTask(Context context)
    {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Podcast... params)
    {
        try
        {
            new FeedSyncTaskProxyBuilder().build(context, params[0]).doInBackground();
        }
        catch (IOException e)
        {
            // TODO
            Log.e(this, e.getMessage());
        }

        return null;
    }
}
