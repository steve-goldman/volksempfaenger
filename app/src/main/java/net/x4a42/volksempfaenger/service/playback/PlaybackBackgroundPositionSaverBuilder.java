package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.os.Handler;

import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelper;
import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelperBuilder;

class PlaybackBackgroundPositionSaverBuilder
{
    public PlaybackBackgroundPositionSaver build(Context context)
    {
        EpisodeDataHelper episodeDataHelper = new EpisodeDataHelperBuilder().build(context);

        return new PlaybackBackgroundPositionSaver(episodeDataHelper, new Handler());
    }
}
