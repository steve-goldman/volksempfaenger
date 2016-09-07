package net.x4a42.volksempfaenger;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class IntentBuilder
{
    public Intent build(String action)
    {
        return new Intent(action);
    }

    public Intent build(String action, Uri uri)
    {
        return new Intent(action, uri);
    }

    public Intent build(Context context, Class<?> cls)
    {
        return new Intent(context, cls);
    }
}
