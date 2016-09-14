package net.x4a42.volksempfaenger;

import android.content.Context;
import android.preference.PreferenceManager;

public class PreferencesBuilder
{
    public Preferences build(Context context)
    {
        return new Preferences(context,
                               PreferenceManager.getDefaultSharedPreferences(context));
    }
}
