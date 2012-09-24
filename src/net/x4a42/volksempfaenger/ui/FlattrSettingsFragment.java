package net.x4a42.volksempfaenger.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;

import net.x4a42.volksempfaenger.Constants;
import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.net.Downloader;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.text.Html;
import android.util.Base64;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FlattrSettingsFragment extends PreferenceFragment implements
		OnPreferenceClickListener {
	private View groupConnectedTo;
	private TextView textConnectedTo;
	private ProgressBar progressConnecting;
	private ListView list;

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
				Uri uri = Uri.parse(callback);
				String code = uri.getQueryParameter("code");
				if (code != null) {
					showHeader();
					progressConnecting.setVisibility(View.VISIBLE);
					textConnectedTo.setText(R.string.flattr_connecting);
					new RetrieveAccessTokenTask().executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, code);
				} else {
					// authorization failed TODO
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
		list.setHeaderDividersEnabled(false);
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
				Log.e(this, accessToken);
				Log.e(this, username);
				progressConnecting.setVisibility(View.INVISIBLE);
				textConnectedTo.setText(Html.fromHtml(getString(
						R.string.flattr_connected_to, username)));
				// TODO save access token
			} else {
				// display error TODO
				Toast.makeText(getActivity(), "Connecting failed",
						Toast.LENGTH_LONG).show();
				hideHeader();
			}
		}

		private String getAccessToken(Reader in) {
			return getJsonStringValue(in, "access_token");
		}

		private String getUsername(Reader in) {
			return getJsonStringValue(in, "username");
		}

		private String getJsonStringValue(Reader in, String key) {
			JsonReader json = new JsonReader(in);
			try {
				try {
					json.beginObject();
					while (json.hasNext()) {
						if (json.nextName().equals(key)) {
							return json.nextString();
						} else {
							json.skipValue();
						}
					}
					json.endObject();
				} finally {
					json.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}
}
