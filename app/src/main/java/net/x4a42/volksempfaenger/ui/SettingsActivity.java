package net.x4a42.volksempfaenger.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.x4a42.volksempfaenger.PreferenceKeys;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.widget.EditText;

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

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preference_download);
			prefScreen = getPreferenceScreen();
			prefInterval = (ListPreference) prefScreen
					.findPreference(PreferenceKeys.DOWNLOAD_INTERVAL);

			// TODO(#113) Use NumberPicker instead
			EditText concurrentEditText = ((EditTextPreference) prefScreen
					.findPreference(PreferenceKeys.DOWNLOAD_CONCURRENT))
					.getEditText();
			concurrentEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
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
			}
		}

	}

	public static class StorageFragment extends SettingsFragment implements
			OnSharedPreferenceChangeListener {

		private PreferenceScreen prefScreen;
		private EditTextPreference prefLocation;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preference_storage);
			prefScreen = getPreferenceScreen();
			prefLocation = (EditTextPreference) prefScreen
					.findPreference(PreferenceKeys.STORAGE_LOCATION);
		}

		@Override
		public void onResume() {
			super.onResume();

			CharSequence storageLocation = prefLocation.getText();
			if (storageLocation == null) {
				storageLocation = VolksempfaengerApplication
						.getDefaultStorageLocation();
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
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			if (key.equals(PreferenceKeys.STORAGE_LOCATION)) {
				prefLocation.setSummary(prefLocation.getText());
			}
		}

	}

	public static class AboutFragment extends SettingsFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preference_about);

			PreferenceScreen prefScreen = getPreferenceScreen();
			VolksempfaengerApplication application = (VolksempfaengerApplication) getActivity()
					.getApplication();

			Preference version = prefScreen
					.findPreference(PreferenceKeys.ABOUT_VERSION);
			version.setSummary(application.getVersionName());
		}

	}

	public static class FlattrCallbackProxyActivity extends Activity {
		@Override
		public void onStart() {
			super.onStart();
			Uri uri = getIntent().getData();
			if (uri != null) {
				Intent intent = new Intent(this, SettingsActivity.class);
				intent.putExtra(EXTRA_SHOW_FRAGMENT,
						FlattrSettingsFragment.class.getName());
				Bundle bundle = new Bundle();
				bundle.putString("callback", uri.toString());
				intent.putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle);
				startActivity(intent);
			}
		}
	}

}
