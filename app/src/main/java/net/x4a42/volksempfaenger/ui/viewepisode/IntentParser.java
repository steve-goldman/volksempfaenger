package net.x4a42.volksempfaenger.ui.viewepisode;

import android.content.Intent;

class IntentParser
{
    private final Intent intent;

    public IntentParser(Intent intent)
    {
        this.intent = intent;
    }

    public long getEpisodeId()
    {
        return intent.getLongExtra(ViewEpisodeActivityIntentProvider.EpisodeIdKey, -1);
    }
}
