package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.service.playlistdownload.PlaylistDownloadServiceIntentProvider;
import net.x4a42.volksempfaenger.service.playlistdownload.PlaylistDownloadServiceIntentProviderBuilder;

import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;

class FeedSyncTaskProxyBuilder
{
    private final Context context;

    public FeedSyncTaskProxyBuilder(Context context)
    {
        this.context = context;
    }

    public FeedSyncTaskProxy build(Podcast podcast) throws IOException
    {
        FeedConnectionProvider connectionProvider
                = new FeedConnectionProviderBuilder().build(context, podcast.getFeedUrl());

        HttpURLConnection connection = connectionProvider.get();
        Reader            reader     = new FeedReaderBuilder().build(connection);
        FeedParserWrapper feedParser = new FeedParserWrapper(reader);
        PodcastUpdater    updater    = new PodcastUpdaterBuilder().build(context);

        PlaylistDownloadServiceIntentProvider intentProvider
                = new PlaylistDownloadServiceIntentProviderBuilder().build(context);

        return new FeedSyncTaskProxy(context, podcast, connection, feedParser, updater, intentProvider);
    }
}
