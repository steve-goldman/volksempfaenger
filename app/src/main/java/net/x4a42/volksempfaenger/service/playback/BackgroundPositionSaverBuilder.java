package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.os.Handler;

import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDaoWrapper;

class BackgroundPositionSaverBuilder
{
    public BackgroundPositionSaver build(Context                  context,
                                         PlaybackPositionProvider positionProvider)
    {
        EpisodePositionDaoWrapper episodePositionDao
                = new EpisodePositionDaoBuilder().build(context);

        return new BackgroundPositionSaver(new Handler(), positionProvider, episodePositionDao);
    }
}
