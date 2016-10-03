package net.x4a42.volksempfaenger.service.feedsync;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;

import java.net.HttpURLConnection;

class FeedSyncTaskProxy
{
    private final Podcast           podcast;
    private final HttpURLConnection feedConnection;
    private final FeedParserWrapper feedParser;
    private final PodcastUpdater    podcastUpdater;

    public FeedSyncTaskProxy(Podcast           podcast,
                             HttpURLConnection feedConnection,
                             FeedParserWrapper feedParser,
                             PodcastUpdater    podcastUpdater)
    {
        this.podcast        = podcast;
        this.feedConnection = feedConnection;
        this.feedParser     = feedParser;
        this.podcastUpdater = podcastUpdater;
    }

    public void doInBackground()
    {
        try
        {
            podcastUpdater.update(podcast, feedParser.parse());
        }
        catch (Exception e)
        {
            Log.e(this, "exception", e);
        }
        finally
        {
            feedConnection.disconnect();
        }
    }
}
