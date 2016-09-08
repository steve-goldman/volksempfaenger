package net.x4a42.volksempfaenger;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class PendingIntentBuilder
{
    public PendingIntent buildService(Context context, int requestCode, Intent intent, int flags)
    {
        return PendingIntent.getService(context, requestCode, intent, flags);
    }
}
