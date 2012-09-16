package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.Constants;
import net.x4a42.volksempfaenger.PreferenceKeys;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.EditText;

public class DeprecatedSettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener, OnPreferenceClickListener,
		OnUpPressedCallback {

	private ListPreference prefDownloadInterval;
	// private CheckBoxPreference prefDownloadAuto;
	// private CheckBoxPreference prefDownloadWifi;
	// private CheckBoxPreference prefDownloadCharging;
	private EditTextPreference prefDownloadConcurrent;
	private EditTextPreference prefStorageLocation;
	private Preference prefAboutVersion;
	private Preference prefAboutWebsite;
	private Preference prefAboutLicense;
	private Preference prefAboutFlattr;

	private VolksempfaengerApplication app;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		app = (VolksempfaengerApplication) getApplication();

		PreferenceScreen prefscreen = getPreferenceScreen();
		prefDownloadInterval = (ListPreference) prefscreen
				.findPreference(PreferenceKeys.DOWNLOAD_INTERVAL);
		// prefDownloadAuto = (CheckBoxPreference) prefscreen
		// .findPreference(PreferenceKeys.DOWNLOAD_AUTO);
		// prefDownloadWifi = (CheckBoxPreference) prefscreen
		// .findPreference(PreferenceKeys.DOWNLOAD_WIFI);
		// prefDownloadCharging = (CheckBoxPreference) prefscreen
		// .findPreference(PreferenceKeys.DOWNLOAD_CHARGING);
		prefDownloadConcurrent = (EditTextPreference) prefscreen
				.findPreference(PreferenceKeys.DOWNLOAD_CONCURRENT);
		prefStorageLocation = (EditTextPreference) prefscreen
				.findPreference(PreferenceKeys.STORAGE_LOCATION);
		prefAboutVersion = prefscreen
				.findPreference(PreferenceKeys.ABOUT_VERSION);
		prefAboutWebsite = prefscreen
				.findPreference(PreferenceKeys.ABOUT_WEBSITE);
		prefAboutLicense = prefscreen
				.findPreference(PreferenceKeys.ABOUT_LICENSE);
		prefAboutFlattr = prefscreen
				.findPreference(PreferenceKeys.ABOUT_FLATTR);

		EditText prefDownloadConcurrentEditText = (EditText) prefDownloadConcurrent
				.getEditText();
		prefDownloadConcurrentEditText
				.setInputType(InputType.TYPE_CLASS_NUMBER);

		prefAboutVersion.setSummary(app.getVersionName());
		prefAboutWebsite.setSummary(Constants.URL_WEBSITE);
		prefAboutWebsite.setOnPreferenceClickListener(this);
		prefAboutLicense.setOnPreferenceClickListener(this);
		prefAboutFlattr.setOnPreferenceClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

		CharSequence downloadInterval = prefDownloadInterval.getEntry();
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

		CharSequence storageLocation = prefStorageLocation.getText();
		if (storageLocation == null) {
			storageLocation = VolksempfaengerApplication
					.getDefaultStorageLocation();
		}

		prefDownloadInterval.setSummary(downloadInterval);
		prefStorageLocation.setSummary(storageLocation);
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
					Uri.parse(Constants.URL_WEBSITE)));
			return true;
		} else if (pref == prefAboutLicense) {
			Intent intent = new Intent(this, LicenseActivity.class);
			startActivity(intent);
		} else if (pref == prefAboutFlattr) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri
					.parse("http://flattr.com/thing/735145/Volksempfanger"));
			startActivity(intent);
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return ActivityHelper.handleGlobalMenu(this, item);
	}

	@Override
	public void onUpPressed() {
		NavUtils.navigateUpFromSameTask(this);
	}

}
