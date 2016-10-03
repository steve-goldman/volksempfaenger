package net.x4a42.volksempfaenger;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences
{
    private final Context           context;
    private final SharedPreferences preferences;

    public Preferences(Context           context,
                       SharedPreferences preferences)
    {
        this.context     = context;
        this.preferences = preferences;
    }

    public boolean downloadWifiOnly()
    {
        return getBoolean(PreferenceKeys.DOWNLOAD_WIFI,
                          context.getString(R.string.settings_default_download_wifi));
    }

    public boolean downloadChargingOnly()
    {
        return getBoolean(PreferenceKeys.DOWNLOAD_CHARGING,
                          context.getString(R.string.settings_default_download_charging));
    }

    public int getDownloadedQueueCount()
    {
        return getInt(PreferenceKeys.DOWNLOADED_QUEUE_COUNT,
                      context.getResources().getInteger(R.integer.default_downloaded_queue_count));
    }

    private boolean getBoolean(String key, String defaultValue)
    {
        return getBoolean(key, Boolean.valueOf(defaultValue));
    }

    private boolean getBoolean(String key, boolean defaultValue)
    {
        return preferences.getBoolean(key, defaultValue);
    }

    private int getInt(String key, int defaultValue)
    {
        return Integer.parseInt(preferences.getString(key, "" + defaultValue));
    }
}
