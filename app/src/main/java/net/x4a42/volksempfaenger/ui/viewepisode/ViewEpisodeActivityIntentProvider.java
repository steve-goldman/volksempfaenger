package net.x4a42.volksempfaenger.ui.viewepisode;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;

public class ViewEpisodeActivityIntentProvider
{
    private final Context       context;
    private final IntentBuilder intentBuilder;
    public static final String  EpisodeIdKey = "episodeId";

    public ViewEpisodeActivityIntentProvider(Context       context,
                                             IntentBuilder intentBuilder)
    {
        this.context       = context;
        this.intentBuilder = intentBuilder;
    }

    public Intent getIntent(Uri episodeUri)
    {
        return intentBuilder.build(context, ViewEpisodeActivity.class).setData(episodeUri);
    }

    public Intent getIntent(Episode episode)
    {
        return intentBuilder.build(context, ViewEpisodeActivity.class)
                .putExtra(EpisodeIdKey, episode.get_id());
    }
}
