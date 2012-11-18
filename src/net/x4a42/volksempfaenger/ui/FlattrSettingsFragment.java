package net.x4a42.volksempfaenger.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;

import net.x4a42.volksempfaenger.Constants;
import net.x4a42.volksempfaenger.PreferenceKeys;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.net.Downloader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FlattrSettingsFragment extends PreferenceFragment implements
		OnPreferenceClickListener {
	private View groupConnectedTo;
	private TextView textConnectedTo;
	private ProgressBar progressConnecting;
	private ListView list;
	private SharedPreferences prefs;

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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		prefs = ((VolksempfaengerApplication) getActivity().getApplication())
				.getSharedPreferences();
	}

	@Override
	public void onStart() {
		super.onStart();

		String username = prefs.getString(PreferenceKeys.FLATTR_USERNAME, null);
		if (username == null) {
			hideHeader();
		} else {
			showHeader();
			setConnectedTo(username);
		}

		Bundle bundle = getArguments();
		if (bundle != null) {
			String callback = bundle.getString("callback");
			if (callback != null) {
				Uri uri = Uri.parse(callback);
				String code = uri.getQueryParameter("code");
				if (code != null) {
					showHeader();
					progressConnecting.setVisibility(View.VISIBLE);
					textConnectedTo.setText(R.string.flattr_connecting);
					new RetrieveAccessTokenTask().executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, code);
				} else {
					displayAuthorizationError();
				}
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		list = (ListView) v.findViewById(android.R.id.list);
		View header = inflater.inflate(R.layout.flattr_settings_header, list,
				false);
		list.addHeaderView(header);
		groupConnectedTo = header.findViewById(R.id.flattrConnectedToGroup);
		textConnectedTo = (TextView) header
				.findViewById(R.id.flattrConnectedTo);
		progressConnecting = (ProgressBar) header
				.findViewById(R.id.flattrAuthProgressBar);
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

	private void showHeader() {
		groupConnectedTo.setVisibility(View.VISIBLE);
		textConnectedTo.setVisibility(View.VISIBLE);
		progressConnecting.setVisibility(View.INVISIBLE);
		list.setHeaderDividersEnabled(true);
	}

	private void hideHeader() {
		groupConnectedTo.setVisibility(View.GONE);
		textConnectedTo.setVisibility(View.GONE);
		progressConnecting.setVisibility(View.GONE);
		list.setHeaderDividersEnabled(false);
	}

	private void setConnectedTo(String username) {
		textConnectedTo.setText(Html.fromHtml(getString(
				R.string.flattr_connected_to, username)));
	}

	private void displayAuthorizationError() {
		ActivityHelper.buildErrorDialog(getActivity(),
				getString(R.string.title_error_flattr_authorization),
				getString(R.string.message_error_flattr_authorization), null)
				.show();
	}

	private class RetrieveAccessTokenTask extends
			AsyncTask<String, Void, String[]> {

		@Override
		protected String[] doInBackground(String... params) {
			String code = params[0];
			String body = String
					.format("{\"grant_type\": \"authorization_code\", \"redirect_uri\": \"%s\", \"code\": \"%s\"}",
							Constants.FLATTR_REDIRECT_URI, code);
			Downloader downloader = new Downloader(getActivity());
			String accessToken;
			try {
				HttpURLConnection connection = downloader
						.getConnection("https://flattr.com/oauth/token");
				try {
					connection.setRequestProperty("Content-Type",
							"application/json");
					String auth = Base64
							.encodeToString(
									(Constants.FLATTR_OAUTH_TOKEN + ":" + Constants.FLATTR_OAUTH_SECRET)
											.getBytes(), Base64.NO_WRAP);
					connection.setRequestProperty("Authorization", "Basic "
							+ auth);
					connection.setDoOutput(true);
					connection.setFixedLengthStreamingMode(body.length());
					OutputStreamWriter bodyStreamWriter = new OutputStreamWriter(
							connection.getOutputStream());
					try {
						bodyStreamWriter.write(body);
					} finally {
						bodyStreamWriter.close();
					}
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(
										connection.getInputStream()));
						accessToken = getAccessToken(reader);
					} else {
						return null;
					}
				} finally {
					connection.disconnect();
				}

			} catch (IOException e) {
				return null;
			}

			// try out access token by getting the username
			String username;
			try {
				HttpURLConnection connection = downloader
						.getConnection("https://api.flattr.com/rest/v2/user");
				try {
					connection.setRequestProperty("Authorization", "Bearer "
							+ accessToken);
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(
										connection.getInputStream()));
						username = getUsername(reader);
					} else {
						return null;
					}
				} finally {
					connection.disconnect();
				}
			} catch (IOException e) {
				return null;
			}
			return new String[] { accessToken, username };
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (result != null) {
				String accessToken = result[0];
				String username = result[1];
				progressConnecting.setVisibility(View.INVISIBLE);
				setConnectedTo(username);
				Editor editor = prefs.edit();
				editor.putString(PreferenceKeys.FLATTR_USERNAME, username);
				editor.putString(PreferenceKeys.FLATTR_ACCESS_TOKEN,
						accessToken);
				if (!editor.commit()) {
					displayAuthorizationError();
				}
			} else {
				displayAuthorizationError();
				hideHeader();
			}
		}

		private String getAccessToken(Reader in) {
			return Utils.getJsonStringValue(in, "access_token");
		}

		private String getUsername(Reader in) {
			return Utils.getJsonStringValue(in, "username");
		}

	}
}
