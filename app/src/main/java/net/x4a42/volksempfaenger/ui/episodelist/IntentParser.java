package net.x4a42.volksempfaenger.ui.episodelist;

import android.content.Intent;

import net.x4a42.volksempfaenger.ui.episodelist.view.EpisodeListActivityIntentProvider;

class IntentParser
{
    private final Intent intent;

    public IntentParser(Intent intent)
    {
        this.intent = intent;
    }

    public long getPodcastId()
    {
        return intent.getLongExtra(EpisodeListActivityIntentProvider.PodcastIdKey, -1);
    }
}
