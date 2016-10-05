package net.x4a42.volksempfaenger.service.playback;

import android.os.Handler;

import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDaoWrapper;

class BackgroundPositionSaverBuilder
{
    public BackgroundPositionSaver build(PlaybackPositionProvider  positionProvider,
                                         EpisodePositionDaoWrapper episodePositionDao)
    {
        return new BackgroundPositionSaver(new Handler(), positionProvider, episodePositionDao);
    }
}
