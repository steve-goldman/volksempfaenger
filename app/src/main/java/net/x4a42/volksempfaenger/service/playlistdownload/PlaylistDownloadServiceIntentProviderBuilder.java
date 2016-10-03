package net.x4a42.volksempfaenger.service.playlistdownload;

import android.content.Context;

import net.x4a42.volksempfaenger.IntentBuilder;

public class PlaylistDownloadServiceIntentProviderBuilder
{
    public PlaylistDownloadServiceIntentProvider build(Context context)
    {
        return new PlaylistDownloadServiceIntentProvider(context, new IntentBuilder());
    }
}
