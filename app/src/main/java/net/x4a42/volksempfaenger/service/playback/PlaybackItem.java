package net.x4a42.volksempfaenger.service.playback;

import android.net.Uri;

public class PlaybackItem
{
    private final Uri    episodeUri;
    private final Uri    uriTime;
    private final Uri    podcastUri;
    private final String path;
    private final String title;
    private final String podcastTitle;
    private final int    durationListenedAtStart;
    private final long   podcastId;

    public PlaybackItem(Uri    episodeUri,
                        Uri    uriTime,
                        Uri    podcastUri,
                        String path,
                        String title,
                        String podcastTitle,
                        int    durationListenedAtStart,
                        long   podcastId)
    {
        this.episodeUri              = episodeUri;
        this.uriTime                 = uriTime;
        this.podcastUri              = podcastUri;
        this.path                    = path;
        this.title                   = title;
        this.podcastTitle            = podcastTitle;
        this.durationListenedAtStart = durationListenedAtStart;
        this.podcastId               = podcastId;
    }

    public Uri getEpisodeUri()
    {
        return episodeUri;
    }

    public Uri getUriTime()
    {
        return uriTime;
    }

    public Uri getPodcastUri()
    {
        return podcastUri;
    }

    public String getPath()
    {
        return path;
    }

    public String getTitle()
    {
        return title;
    }

    public String getPodcastTitle()
    {
        return podcastTitle;
    }

    public int getDurationListenedAtStart()
    {
        return durationListenedAtStart;
    }

    public long getPodcastId()
    {
        return podcastId;
    }
}
