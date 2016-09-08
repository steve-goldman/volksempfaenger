package net.x4a42.volksempfaenger.service.playback;

import android.os.Handler;

class PlaybackBackgroundPositionSaver implements Runnable
{
    static final int                    Interval = 3000;
    private final PlaybackPositionSaver saver;
    private final Handler               handler;
    private PlaybackItem                playbackItem;
    private PlaybackPositionProvider    positionProvider;

    public PlaybackBackgroundPositionSaver(PlaybackPositionSaver saver,
                                           Handler               handler)
    {
        this.saver   = saver;
        this.handler = handler;
    }

    public void start(PlaybackItem playbackItem, PlaybackPositionProvider positionProvider)
    {
        this.playbackItem     = playbackItem;
        this.positionProvider = positionProvider;

        handler.post(this);
    }

    public void stop()
    {
        saver.save(playbackItem, positionProvider.getPosition());
        handler.removeCallbacks(this);
    }

    @Override
    public void run()
    {
        saver.save(playbackItem, positionProvider.getPosition());
        handler.postDelayed(this, Interval);
    }
}
