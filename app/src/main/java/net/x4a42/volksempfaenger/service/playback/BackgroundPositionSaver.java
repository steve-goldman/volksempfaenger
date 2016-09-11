package net.x4a42.volksempfaenger.service.playback;

import android.net.Uri;
import android.os.Handler;

import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelper;

class BackgroundPositionSaver implements Runnable
{
    static final int                 Interval = 3000;
    private final EpisodeDataHelper  episodeDataHelper;
    private final Handler            handler;
    private Uri                      episodeUri;
    private PlaybackPositionProvider positionProvider;

    public BackgroundPositionSaver(EpisodeDataHelper episodeDataHelper,
                                   Handler handler)
    {
        this.episodeDataHelper = episodeDataHelper;
        this.handler           = handler;
    }

    public void start(Uri episodeUri, PlaybackPositionProvider positionProvider)
    {
        this.episodeUri       = episodeUri;
        this.positionProvider = positionProvider;

        episodeDataHelper.markListening(episodeUri);

        handler.post(this);
    }

    public void stop(boolean resetPosition)
    {
        if (resetPosition)
        {
            episodeDataHelper.setDurationListened(episodeUri, 0);
        }
        else
        {
            save();
        }

        handler.removeCallbacks(this);
    }

    @Override
    public void run()
    {
        save();
        handler.postDelayed(this, Interval);
    }

    private void save()
    {
        episodeDataHelper.setDurationListened(episodeUri, positionProvider.getPosition());
    }
}
