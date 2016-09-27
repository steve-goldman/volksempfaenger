package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.os.Handler;

import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelper;
import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelperBuilder;

class BackgroundPositionSaverBuilder
{
    public BackgroundPositionSaver build(PlaybackPositionProvider positionProvider)
    {
        return new BackgroundPositionSaver(new Handler(), positionProvider);
    }
}
