package net.x4a42.volksempfaenger.ui.episodelist.view;

import android.content.Context;
import android.content.Intent;

import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.ui.episodelist.EpisodeListActivity;

public class EpisodeListActivityIntentProvider
{
    private final Context       context;
    private final IntentBuilder intentBuilder;
    public static final String  PodcastIdKey = "podcastId";

    public EpisodeListActivityIntentProvider(Context       context,
                                             IntentBuilder intentBuilder)
    {
        this.context       = context;
        this.intentBuilder = intentBuilder;
    }

    public Intent getIntent(Podcast podcast)
    {
        return intentBuilder.build(context, EpisodeListActivity.class)
                .putExtra(PodcastIdKey, podcast.get_id());
    }
}
