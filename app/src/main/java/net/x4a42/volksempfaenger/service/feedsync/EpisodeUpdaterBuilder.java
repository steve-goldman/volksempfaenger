package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;

class EpisodeUpdaterBuilder
{
    public EpisodeUpdater build(Context context)
    {
        EpisodeDaoWrapper episodeDao = new EpisodeDaoBuilder().build(context);
        EnclosureUpdater  enclosureUpdater = new EnclosureUpdaterBuilder().build(context);

        return new EpisodeUpdater(episodeDao, enclosureUpdater);
    }
}
