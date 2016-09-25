package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastPathProvider;
import net.x4a42.volksempfaenger.feedparser.Feed;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

class LogoDownloaderBuilder
{
    private final Context context;

    public LogoDownloaderBuilder(Context context)
    {
        this.context = context;
    }

    public LogoDownloader build(Podcast podcast, Feed feed) throws IOException
    {
        HttpURLConnection connection
                = new LogoConnectionProviderBuilder().build(context, feed.image).get();

        InputStream inputStream = connection.getInputStream();

        PodcastPathProvider podcastPathProvider
                = new PodcastPathProvider(context);

        File targetFile = podcastPathProvider.getLogoFile(podcast);

        OutputStream outputStream
                = new BufferedOutputStream(new FileOutputStream(targetFile));

        return new LogoDownloader(inputStream, outputStream);
    }
}
