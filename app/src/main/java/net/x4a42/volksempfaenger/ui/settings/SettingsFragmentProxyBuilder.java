package net.x4a42.volksempfaenger.ui.settings;

import android.preference.PreferenceFragment;

import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.PreferencesBuilder;
import net.x4a42.volksempfaenger.alarm.SyncAllAlarmManager;
import net.x4a42.volksempfaenger.alarm.SyncAllAlarmManagerBuilder;
import net.x4a42.volksempfaenger.event.preferencechanged.PreferenceChangedEventBroadcaster;
import net.x4a42.volksempfaenger.event.preferencechanged.PreferenceChangedEventBroadcasterBuilder;
import net.x4a42.volksempfaenger.event.preferencechanged.PreferenceChangedEventProvider;
import net.x4a42.volksempfaenger.service.playlistdownload.PlaylistDownloadServiceIntentProvider;
import net.x4a42.volksempfaenger.service.playlistdownload.PlaylistDownloadServiceIntentProviderBuilder;

class SettingsFragmentProxyBuilder
{
    public SettingsFragmentProxy build(PreferenceFragment fragment)
    {
        SyncAllAlarmManager syncAllAlarmManager
                = new SyncAllAlarmManagerBuilder().build(fragment.getActivity());

        Preferences preferences = new PreferencesBuilder().build(fragment.getActivity());

        PlaylistDownloadServiceIntentProvider playlistDownloadIntentProvider
                = new PlaylistDownloadServiceIntentProviderBuilder().build(fragment.getActivity());

        PreferenceChangedEventBroadcaster eventBroadcaster
                = new PreferenceChangedEventBroadcasterBuilder().build();

        PreferenceChangedEventProvider eventProvider
                = new PreferenceChangedEventProvider();

        return new SettingsFragmentProxy(fragment,
                                         preferences,
                                         syncAllAlarmManager,
                                         playlistDownloadIntentProvider,
                                         eventBroadcaster,
                                         eventProvider);
    }
}
