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

    public long getPlaylistCurrentPosition()
    {
        return getLong(PreferenceKeys.PLAYLIST_CURRENT_POSITION, 1);
    }

    public void setPlaylistCurrentPosition(long newPosition)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(PreferenceKeys.PLAYLIST_CURRENT_POSITION, newPosition);
        editor.apply();
    }

    private boolean getBoolean(String key, String defaultValue)
    {
        return getBoolean(key, Boolean.valueOf(defaultValue));
    }

    private boolean getBoolean(String key, boolean defaultValue)
    {
        return preferences.getBoolean(key, defaultValue);
    }

    private long getLong(String key, long defaultValue)
    {
        return preferences.getLong(key, defaultValue);
    }
}
