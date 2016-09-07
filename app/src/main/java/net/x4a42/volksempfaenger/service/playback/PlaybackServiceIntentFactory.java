package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.content.Intent;

public class PlaybackServiceIntentFactory
{
    private final Context context;

    public PlaybackServiceIntentFactory(Context context)
    {
        this.context = context;
    }

    public Intent create()
    {
        return new Intent(context, PlaybackService.class);
    }

    public Intent create(String action)
    {
        return create().setAction(action);
    }
}
