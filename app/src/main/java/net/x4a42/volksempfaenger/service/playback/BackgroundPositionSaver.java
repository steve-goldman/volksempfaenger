package net.x4a42.volksempfaenger.service.playback;

import android.os.Handler;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePosition;
import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDaoWrapper;

class BackgroundPositionSaver implements Runnable
{
    static final int                        Interval = 3000;
    private final Handler                   handler;
    private final PlaybackPositionProvider  positionProvider;
    private final EpisodePositionDaoWrapper episodePositionDao;
    private EpisodePosition                 episodePosition;

    public BackgroundPositionSaver(Handler                   handler,
                                   PlaybackPositionProvider  positionProvider,
                                   EpisodePositionDaoWrapper episodePositionDao)
    {
        this.handler            = handler;
        this.positionProvider   = positionProvider;
        this.episodePositionDao = episodePositionDao;
    }

    public void start(Episode episode)
    {
        episodePosition = episodePositionDao.getOrCreate(episode);

        // TODO: mark listening

        handler.post(this);
    }

    public void stop(boolean resetPosition)
    {
        if (episodePosition == null)
        {
            return;
        }

        if (resetPosition)
        {
            episodePositionDao.delete(episodePosition);
        }
        else
        {
            save();
        }

        handler.removeCallbacks(this);
        episodePosition = null;
    }

    @Override
    public void run()
    {
        save();
        handler.postDelayed(this, Interval);
    }

    private void save()
    {
        episodePosition.setPosition(positionProvider.getPosition());
        episodePositionDao.insertOrReplace(episodePosition);
    }
}
