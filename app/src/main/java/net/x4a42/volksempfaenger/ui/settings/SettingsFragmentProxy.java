package net.x4a42.volksempfaenger.ui.settings;

import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import net.x4a42.volksempfaenger.PreferenceKeys;
import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.alarm.SyncAllAlarmManager;
import net.x4a42.volksempfaenger.event.preferencechanged.PreferenceChangedEventBroadcaster;
import net.x4a42.volksempfaenger.event.preferencechanged.PreferenceChangedEventProvider;
import net.x4a42.volksempfaenger.service.playlistdownload.PlaylistDownloadServiceIntentProvider;

class SettingsFragmentProxy implements Preference.OnPreferenceChangeListener
{
    private final PreferenceFragment                    fragment;
    private final Preferences                           preferences;
    private final SyncAllAlarmManager                   syncAllAlarmManager;
    private final PlaylistDownloadServiceIntentProvider playlistDownloadIntentProvider;
    private final PreferenceChangedEventBroadcaster     eventBroadcaster;
    private final PreferenceChangedEventProvider        eventProvider;
    private ListPreference                              intervalList;
    private CheckBoxPreference                          downloadWifiOnly;
    private CheckBoxPreference                          streamWifiOnly;
    private EditTextPreference                          queueCount;

    public SettingsFragmentProxy(PreferenceFragment                    fragment,
                                 Preferences                           preferences,
                                 SyncAllAlarmManager                   syncAllAlarmManager,
                                 PlaylistDownloadServiceIntentProvider playlistDownloadIntentProvider,
                                 PreferenceChangedEventBroadcaster     eventBroadcaster,
                                 PreferenceChangedEventProvider        eventProvider)
    {
        this.fragment                       = fragment;
        this.preferences                    = preferences;
        this.syncAllAlarmManager            = syncAllAlarmManager;
        this.playlistDownloadIntentProvider = playlistDownloadIntentProvider;
        this.eventBroadcaster               = eventBroadcaster;
        this.eventProvider                  = eventProvider;
    }

    public void onCreate()
    {
        fragment.addPreferencesFromResource(R.xml.settings);
        intervalList     = (ListPreference)     findPreference(PreferenceKeys.DOWNLOAD_INTERVAL);
        downloadWifiOnly = (CheckBoxPreference) findPreference(PreferenceKeys.DOWNLOAD_WIFI);
        streamWifiOnly   = (CheckBoxPreference) findPreference(PreferenceKeys.STREAM_WIFI);
        queueCount       = (EditTextPreference) findPreference(PreferenceKeys.DOWNLOADED_QUEUE_COUNT);
    }

    public void onResume()
    {
        updateInterval(preferences.getSyncInterval());
        updateQueueCount(preferences.getDownloadedQueueCount());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        switch (preference.getKey())
        {
            case PreferenceKeys.DOWNLOAD_INTERVAL:
                updateInterval(Long.parseLong((String) newValue));
                syncAllAlarmManager.reschedule();
                break;
            case PreferenceKeys.DOWNLOAD_WIFI:
                fragment.getActivity().startService(playlistDownloadIntentProvider.getRunIntent());
                break;
            case PreferenceKeys.DOWNLOADED_QUEUE_COUNT:
                updateQueueCount(Integer.parseInt((String) newValue));
                break;
        }
        eventBroadcaster.broadcast(eventProvider.get());
        return true;
    }

    private void updateInterval(long interval)
    {
        intervalList.setSummary(preferences.getSyncIntervalStr(interval));
    }

    private void updateQueueCount(int length)
    {
        String numEpisodes = (length == 1) ? "1 episode" : length + " episodes";
        queueCount.setSummary(String.format(fragment.getString(R.string.settings_downloaded_queue_count_summary), numEpisodes));
    }

    private Preference findPreference(String key)
    {
        Preference preference = fragment.findPreference(key);
        preference.setOnPreferenceChangeListener(this);
        return preference;
    }
}
