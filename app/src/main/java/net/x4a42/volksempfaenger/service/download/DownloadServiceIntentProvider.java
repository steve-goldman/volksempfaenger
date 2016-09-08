package net.x4a42.volksempfaenger.service.download;

import android.content.Context;
import android.content.Intent;

import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.service.DownloadService;

public class DownloadServiceIntentProvider
{
    public static final String  IdKey            = "id";
    public static final String  ForceDownloadKey = "forceDownload";
    private final Context       context;
    private final IntentBuilder intentBuilder;

    public DownloadServiceIntentProvider(Context       context,
                                         IntentBuilder intentBuilder)
    {
        this.context       = context;
        this.intentBuilder = intentBuilder;
    }

    public Intent getDownloadIntent(long[] episodeId, boolean forceDownload)
    {
        return intentBuilder.build(context, DownloadService.class)
                .putExtra(IdKey, episodeId)
                .putExtra(ForceDownloadKey, forceDownload);
    }
}
