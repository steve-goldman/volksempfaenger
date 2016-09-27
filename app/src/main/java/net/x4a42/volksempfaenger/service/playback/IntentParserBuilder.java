package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;

class IntentParserBuilder
{
    public IntentParser build(Context context)
    {
        EpisodeDaoWrapper episodeDao = new EpisodeDaoBuilder().build(context);
        return new IntentParser(episodeDao);
    }
}
