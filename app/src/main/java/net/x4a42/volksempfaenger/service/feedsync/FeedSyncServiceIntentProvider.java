package net.x4a42.volksempfaenger.service.feedsync;

import android.content.Context;
import android.content.Intent;

import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;

public class FeedSyncServiceIntentProvider
{
    public static final String  PodcastIdKey = "podcastId";
    private final Context       context;
    private final IntentBuilder intentBuilder;

    public FeedSyncServiceIntentProvider(Context       context,
                                         IntentBuilder intentBuilder)
    {
        this.context       = context;
        this.intentBuilder = intentBuilder;
    }

    public Intent getSyncIntent(Podcast podcast)
    {
        return intentBuilder.build(context, FeedSyncService.class)
                .setAction(FeedSyncService.ActionSync)
                .putExtra(PodcastIdKey, podcast.get_id());
    }
}
