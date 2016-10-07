package net.x4a42.volksempfaenger.service.playlistdownload;

import android.content.Context;
import android.content.Intent;

import net.x4a42.volksempfaenger.IntentBuilder;

public class PlaylistDownloadServiceIntentProvider
{
    private final Context       context;
    private final IntentBuilder intentBuilder;

    public PlaylistDownloadServiceIntentProvider(Context       context,
                                                 IntentBuilder intentBuilder)
    {
        this.context       = context;
        this.intentBuilder = intentBuilder;
    }

    public Intent getRunIntent()
    {
        return intentBuilder.build(context, PlaylistDownloadService.class)
                .setAction(PlaylistDownloadService.ActionRun);
    }
}
