package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.Constants;
import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FlattrSettingsFragment extends PreferenceFragment implements
		OnPreferenceClickListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference_flattr);
		Preference authPref = findPreference("flattr_auth");
		authPref.setOnPreferenceClickListener(this);
		authPref.setSummary(getString(R.string.settings_summary_flattr_auth,
				getString(R.string.app_name)));
	}

	@Override
	public void onStart() {
		super.onStart();
		Bundle bundle = getArguments();
		if (bundle != null) {
			String callback = bundle.getString("callback");
			if (callback != null) {
				Log.e(this, callback);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.flattr_settings, container, false);
		return v;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		String authorizeUrl = "https://flattr.com/oauth/authorize?response_type=code&scope=flattr&client_id="
				+ Constants.FLATTR_OAUTH_TOKEN;
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(authorizeUrl));
		startActivity(i);
		return true;
	}
}
