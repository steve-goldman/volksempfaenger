package net.x4a42.volksempfaenger.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import net.x4a42.volksempfaenger.PreferenceKeys;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.alarm.SyncAllAlarmManagerBuilder;
import net.x4a42.volksempfaenger.event.preferencechanged.PreferenceChangedEvent;
import net.x4a42.volksempfaenger.event.preferencechanged.PreferenceChangedEventBroadcaster;
import net.x4a42.volksempfaenger.event.preferencechanged.PreferenceChangedEventBroadcasterBuilder;
import net.x4a42.volksempfaenger.service.playlistdownload.PlaylistDownloadServiceIntentProviderBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class SettingsActivity extends PreferenceActivity
{
	private static final Collection<String> validFragments = new HashSet<>();

	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preference_headers, target);

		for (Header header : target)
		{
			validFragments.add(header.fragment);
		}
	}

	@Override
	public void invalidateHeaders()
	{
		validFragments.clear();
	}

	@Override
	protected boolean isValidFragment(String fragmentName)
	{
		return validFragments.contains(fragmentName);
	}

	public static class DownloadFragment extends SettingsFragment implements
			OnSharedPreferenceChangeListener {

		private PreferenceScreen prefScreen;
		private ListPreference prefInterval;
		private PreferenceChangedEventBroadcaster broadcaster;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			broadcaster = new PreferenceChangedEventBroadcasterBuilder().build();
			addPreferencesFromResource(R.xml.preference_download);
			prefScreen = getPreferenceScreen();
			prefInterval = (ListPreference) prefScreen
					.findPreference(PreferenceKeys.DOWNLOAD_INTERVAL);
		}

		@Override
		public void onResume() {
			super.onResume();

			CharSequence downloadInterval = prefInterval.getEntry();
			if (downloadInterval == null) {
				downloadInterval = getString(R.string.settings_default_download_interval);

				String[] choices = getResources().getStringArray(
						R.array.settings_interval_values);
				for (int i = 0; i < choices.length; i++) {
					if (downloadInterval.equals(choices)) {
						downloadInterval = getResources().getStringArray(
								R.array.settings_interval_choices)[i];
						break;
					}
				}
			}
			prefInterval.setSummary(downloadInterval);

			prefScreen.getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			super.onPause();
			prefScreen.getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			if (key.equals(PreferenceKeys.DOWNLOAD_INTERVAL)) {
				prefInterval.setSummary(prefInterval.getEntry().toString());
				new SyncAllAlarmManagerBuilder().build(getActivity()).reschedule();
			}
			else if (key.equals(PreferenceKeys.DOWNLOAD_WIFI))
			{
				Intent intent = new PlaylistDownloadServiceIntentProviderBuilder().build(getActivity()).getRunIntent();
				getActivity().startService(intent);
			}
			broadcaster.broadcast(new PreferenceChangedEvent());
		}

	}

	public static class StorageFragment extends SettingsFragment implements
			OnSharedPreferenceChangeListener {

		private PreferenceScreen prefScreen;
		private EditTextPreference prefLocation;
		private EditTextPreference prefQueueCount;
		private PreferenceChangedEventBroadcaster broadcaster;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			broadcaster = new PreferenceChangedEventBroadcasterBuilder().build();
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
			editor.putString(PreferenceKeys.DOWNLOADED_QUEUE_COUNT, "");
			editor.apply();
			addPreferencesFromResource(R.xml.preference_storage);
			prefScreen = getPreferenceScreen();
			prefLocation = (EditTextPreference) prefScreen
					.findPreference(PreferenceKeys.STORAGE_LOCATION);
			prefQueueCount = (EditTextPreference) prefScreen
					.findPreference(PreferenceKeys.DOWNLOADED_QUEUE_COUNT);
		}

		@Override
		public void onResume() {
			super.onResume();

			CharSequence storageLocation = prefLocation.getText();
			if (storageLocation == null) {
				storageLocation = Environment.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_PODCASTS).getAbsolutePath();
			}
			prefLocation.setSummary(storageLocation);

			prefScreen.getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			super.onPause();
			prefScreen.getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if (key.equals(PreferenceKeys.STORAGE_LOCATION)) {
				prefLocation.setSummary(prefLocation.getText());
			}
			else if (key.equals(PreferenceKeys.DOWNLOADED_QUEUE_COUNT))
			{
				try
				{
					//noinspection ResultOfMethodCallIgnored
					Integer.parseInt(prefQueueCount.getText());
				}
				catch (NumberFormatException e)
				{
					String text = "" + getResources().getInteger(R.integer.default_downloaded_queue_count);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString(PreferenceKeys.DOWNLOADED_QUEUE_COUNT, text);
					editor.apply();
					prefQueueCount.setText(text);
				}
			}
			broadcaster.broadcast(new PreferenceChangedEvent());
		}

	}

}
