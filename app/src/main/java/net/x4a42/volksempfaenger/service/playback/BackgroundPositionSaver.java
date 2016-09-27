package net.x4a42.volksempfaenger.service.playback;

import android.os.Handler;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;

class BackgroundPositionSaver implements Runnable
{
    static final int                       Interval = 3000;
    private final Handler                  handler;
    private final PlaybackPositionProvider positionProvider;
    private Episode                        episode;

    public BackgroundPositionSaver(Handler                  handler,
                                   PlaybackPositionProvider positionProvider)
    {
        this.handler          = handler;
        this.positionProvider = positionProvider;
    }

    public void start(Episode episode)
    {
        this.episode          = episode;

        // TODO: mark listening

        handler.post(this);
    }

    public void stop(boolean resetPosition)
    {
        if (resetPosition)
        {
            // TODO: reset duration listened
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
        // TODO: save duration listened
        //episodeDataHelper.setDurationListened(episodeUri, positionProvider.getPosition());
    }
}
