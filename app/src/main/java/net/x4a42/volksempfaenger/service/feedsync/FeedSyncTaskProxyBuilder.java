package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;

import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;

class FeedSyncTaskProxyBuilder
{
    public FeedSyncTaskProxy build(Context context, Podcast podcast) throws IOException
    {
        FeedConnectionProvider connectionProvider
                = new FeedConnectionProviderBuilder().build(context, podcast.getFeedUrl());

        HttpURLConnection connection = connectionProvider.get();
        Reader            reader     = new FeedReaderBuilder().build(connection);
        FeedParserWrapper feedParser = new FeedParserWrapper(reader);
        PodcastUpdater    updater    = new PodcastUpdaterBuilder().build(context);

        return new FeedSyncTaskProxy(podcast, connection, feedParser, updater);
    }
}
