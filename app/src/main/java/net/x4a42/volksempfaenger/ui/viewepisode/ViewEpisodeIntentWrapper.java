package net.x4a42.volksempfaenger.ui.viewepisode;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;

import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;

public class ViewEpisodeIntentWrapper
{
    private final Intent intent;

    public ViewEpisodeIntentWrapper(Intent intent)
    {
        this.intent = intent;
    }

    public Uri getEpisodeUri()
    {
        if (intent.getData() != null)
        {
            return intent.getData();
        }

        return ContentUris.withAppendedId(VolksempfaengerContentProvider.EPISODE_URI, getEpisodeId());
    }

    public long getEpisodeId()
    {
        if (intent.getData() == null)
        {
            return intent.getLongExtra("id", -1);
        }

        return ContentUris.parseId(getEpisodeUri());
    }
}
