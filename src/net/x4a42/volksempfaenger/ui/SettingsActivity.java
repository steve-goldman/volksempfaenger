package net.x4a42.volksempfaenger.ui;

import java.util.List;

import net.x4a42.volksempfaenger.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

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
				bundle.putString("code", uri.getQueryParameter("code"));
				intent.putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle);
				startActivity(intent);
			}
		}
	}

}
