package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.service.playlistdownload.PlaylistDownloadServiceIntentProvider;

import java.net.HttpURLConnection;

class FeedSyncTaskProxy
{
    private final Context                               context;
    private final Podcast                               podcast;
    private final HttpURLConnection                     feedConnection;
    private final FeedParserWrapper                     feedParser;
    private final PodcastUpdater                        podcastUpdater;
    private final PlaylistDownloadServiceIntentProvider intentProvider;

    public FeedSyncTaskProxy(Context                               context,
                             Podcast                               podcast,
                             HttpURLConnection                     feedConnection,
                             FeedParserWrapper                     feedParser,
                             PodcastUpdater                        podcastUpdater,
                             PlaylistDownloadServiceIntentProvider intentProvider)
    {
        this.context        = context;
        this.podcast        = podcast;
        this.feedConnection = feedConnection;
        this.feedParser     = feedParser;
        this.podcastUpdater = podcastUpdater;
        this.intentProvider = intentProvider;
    }

    public void doInBackground()
    {
        try
        {
            podcastUpdater.update(podcast, feedParser.parse());
            context.startService(intentProvider.getRunIntent());
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
