package net.x4a42.volksempfaenger.ui;

import java.util.List;

import net.x4a42.volksempfaenger.PreferenceKeys;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

public class SettingsActivity extends PreferenceActivity {

	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preference_headers, target);
	}

	public static class DownloadFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preference_download);
		}

	}

	public static class StorageFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preference_storage);
		}

	}

	public static class AboutFragment extends PreferenceFragment {

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

}
