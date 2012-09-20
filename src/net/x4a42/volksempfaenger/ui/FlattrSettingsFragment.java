package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.Constants;
import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class FlattrSettingsFragment extends PreferenceFragment implements
		OnClickListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference_flattr);
	}

	@Override
	public void onStart() {
		super.onStart();
		Bundle bundle = getArguments();
		if (bundle != null) {
			String code = bundle.getString("code");
			if (code != null) {
				Log.e(this, code);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.flattr_settings, container, false);
		Button authButton = (Button) v.findViewById(R.id.button_flattr_auth);
		authButton.setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {
		String authorizeUrl = "https://flattr.com/oauth/authorize?response_type=code&scope=flattr&client_id="
				+ Constants.FLATTR_OAUTH_TOKEN;
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(authorizeUrl));
		startActivity(i);
	}
}
