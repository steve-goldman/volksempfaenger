package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;

class IntentParserBuilder
{
    public IntentParser build(Context context)
    {
        PodcastDaoWrapper podcastDao = new PodcastDaoBuilder().build(context);

        return new IntentParser(podcastDao);
    }
}
