package net.Ox4a42.volksempfaenger.ui;

import net.Ox4a42.volksempfaenger.PreferenceKeys;
import net.Ox4a42.volksempfaenger.R;
import net.Ox4a42.volksempfaenger.VolksempfaengerApplication;
import net.Ox4a42.volksempfaenger.VolksempfaengerUrls;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class SettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener, OnPreferenceClickListener {
	private ListPreference prefDownloadInterval;
	private CheckBoxPreference prefDownloadAuto;
	private CheckBoxPreference prefDownloadWifi;
	private CheckBoxPreference prefDownloadCharging;
	private EditTextPreference prefStorageLocation;
	private Preference prefAboutVersion;
	private Preference prefAboutWebsite;
	private Preference prefAboutWiki;
	private Preference prefAboutBugtracker;

	private VolksempfaengerApplication app;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		app = (VolksempfaengerApplication) getApplication();

		PreferenceScreen prefscreen = getPreferenceScreen();
		prefDownloadInterval = (ListPreference) prefscreen
				.findPreference(PreferenceKeys.DOWNLOAD_INTERVAL);
		prefDownloadAuto = (CheckBoxPreference) prefscreen
				.findPreference(PreferenceKeys.DOWNLOAD_AUTO);
		prefDownloadWifi = (CheckBoxPreference) prefscreen
				.findPreference(PreferenceKeys.DOWNLOAD_WIFI);
		prefDownloadCharging = (CheckBoxPreference) prefscreen
				.findPreference(PreferenceKeys.DOWNLOAD_CHARGING);
		prefStorageLocation = (EditTextPreference) prefscreen
				.findPreference(PreferenceKeys.STORAGE_LOCATION);
		prefAboutVersion = prefscreen
				.findPreference(PreferenceKeys.ABOUT_VERSION);
		prefAboutWebsite = prefscreen
				.findPreference(PreferenceKeys.ABOUT_WEBSITE);
		prefAboutWiki = prefscreen
				.findPreference(PreferenceKeys.ABOUT_WIKI);
		prefAboutBugtracker = prefscreen
				.findPreference(PreferenceKeys.ABOUT_BUGTRACKER);

		prefAboutVersion.setSummary(app.getVersionName());
		prefAboutWebsite.setSummary(VolksempfaengerUrls.WEBSITE);
		prefAboutWebsite.setOnPreferenceClickListener(this);
		prefAboutWiki.setSummary(VolksempfaengerUrls.WIKI);
		prefAboutWiki.setOnPreferenceClickListener(this);
		prefAboutBugtracker.setSummary(VolksempfaengerUrls.BUGTRACKER);
		prefAboutBugtracker.setOnPreferenceClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

		prefDownloadInterval.setSummary(prefDownloadInterval.getEntry()
				.toString());
		prefStorageLocation.setSummary(prefStorageLocation.getText());
	}

	@Override
	protected void onPause() {
		super.onPause();

		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if (key.equals(PreferenceKeys.DOWNLOAD_INTERVAL)) {
			prefDownloadInterval.setSummary(prefDownloadInterval.getEntry()
					.toString());
		} else if (key.equals(PreferenceKeys.STORAGE_LOCATION)) {
			prefStorageLocation.setSummary(prefStorageLocation.getText());
		}
	}

	public boolean onPreferenceClick(Preference pref) {
		if (pref == prefAboutWebsite) {
			startActivity(new Intent("android.intent.action.VIEW",
					Uri.parse(VolksempfaengerUrls.WEBSITE)));
			return true;
		} else if (pref == prefAboutWiki) {
			startActivity(new Intent("android.intent.action.VIEW",
					Uri.parse(VolksempfaengerUrls.WIKI)));
			return true;
		} else if (pref == prefAboutBugtracker) {
			startActivity(new Intent("android.intent.action.VIEW",
					Uri.parse(VolksempfaengerUrls.BUGTRACKER)));
			return true;
		}
		return false;
	}
}