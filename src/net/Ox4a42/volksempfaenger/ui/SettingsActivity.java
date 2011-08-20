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

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener, OnPreferenceClickListener {
	private ListPreference prefDownloadInterval;
	private CheckBoxPreference prefDownloadAuto;
	private CheckBoxPreference prefDownloadWifi;
	private CheckBoxPreference prefDownloadCharging;
	private EditTextPreference prefStorageLocation;
	private Preference prefAboutVersion;
	private Preference prefAboutWebsite;
	
	private VolksempfaengerApplication app;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        addPreferencesFromResource(R.xml.settings);
        
        app = (VolksempfaengerApplication) getApplication();
        
        PreferenceScreen prefscreen = getPreferenceScreen();
        prefscreen.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        prefDownloadInterval = (ListPreference) prefscreen.findPreference(PreferenceKeys.DOWNLOAD_INTERVAL);
        prefDownloadAuto = (CheckBoxPreference) prefscreen.findPreference(PreferenceKeys.DOWNLOAD_AUTO);
        prefDownloadWifi = (CheckBoxPreference) prefscreen.findPreference(PreferenceKeys.DOWNLOAD_WIFI);
        prefDownloadCharging = (CheckBoxPreference) prefscreen.findPreference(PreferenceKeys.DOWNLOAD_CHARGING);
        prefStorageLocation = (EditTextPreference) prefscreen.findPreference(PreferenceKeys.STORAGE_LOCATION);
        prefAboutVersion = prefscreen.findPreference(PreferenceKeys.ABOUT_VERSION);
        prefAboutWebsite = prefscreen.findPreference(PreferenceKeys.ABOUT_WEBSITE);
        
        prefDownloadInterval.setSummary(prefDownloadInterval.getEntry().toString());
        prefStorageLocation.setSummary(prefStorageLocation.getText());
        prefAboutVersion.setSummary(app.getVersionName());
        prefAboutWebsite.setSummary(VolksempfaengerUrls.WEBSITE);
        prefAboutWebsite.setOnPreferenceClickListener(this);
    }

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if (key.equals(PreferenceKeys.DOWNLOAD_INTERVAL)) {
	        prefDownloadInterval.setSummary(prefDownloadInterval.getEntry().toString());
		} else if (key.equals(PreferenceKeys.STORAGE_LOCATION)) {
			prefStorageLocation.setSummary(prefStorageLocation.getText());
		}
	}

	public boolean onPreferenceClick(Preference pref) {
		if (pref == prefAboutWebsite) {
			startActivity(new Intent("android.intent.action.VIEW", Uri.parse(VolksempfaengerUrls.WEBSITE)));
			return true;
		}
		return false;
	}
}