package net.x4a42.volksempfaenger.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.feedparser.GpodderJsonReader;
import net.x4a42.volksempfaenger.net.Downloader;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.JsonReader;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class AddSubscriptionActivity extends Activity implements
		OnUpPressedCallback, OnItemClickListener, OnFocusChangeListener,
		OnEditorActionListener, OnClickListener, OnItemSelectedListener {

	private ImageLoader imageLoader;
	private final static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.default_logo)
			.showImageForEmptyUri(R.drawable.default_logo).cacheInMemory()
			.cacheOnDisc().imageScaleType(ImageScaleType.POWER_OF_2).build();
	private ListView popularList;
	private AutoCompleteTextView searchEntry;
	private ImageButton searchButton;
	private View loadingIndicator;
	private Spinner tagSpinner;
	private ArrayAdapter<String> tagAdapter;
	private LoadPopularListTask loadTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.title_add_subscription);
		setContentView(R.layout.add_subscription);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		searchEntry = (AutoCompleteTextView) findViewById(R.id.entry_search);
		searchEntry.setOnFocusChangeListener(this);
		searchEntry.setOnEditorActionListener(this);

		searchButton = (ImageButton) findViewById(R.id.button_search);
		searchButton.setOnClickListener(this);

		popularList = (ListView) findViewById(R.id.popular_list);
		popularList.setOnItemClickListener(this);

		loadingIndicator = findViewById(R.id.loading);

		tagSpinner = (Spinner) findViewById(R.id.tag_spinner);
		tagAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item);
		tagAdapter.add(getString(R.string.title_tag_all));
		tagSpinner.setAdapter(tagAdapter);
		tagSpinner.setOnItemSelectedListener(this);

		imageLoader = ((VolksempfaengerApplication) getApplication()).imageLoader;

		loadTask = new LoadPopularListTask(getToplistUrl());
		loadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		new LoadTagsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void onStart() {
		super.onStart();

		Uri data = getIntent().getData();
		if (data != null) {
			searchEntry.setText(data.toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ActivityHelper.addGlobalMenu(this, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		ExternalStorageHelper.assertExternalStorageWritable(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		default:
			return ActivityHelper.handleGlobalMenu(this, item);
		}
	}

	@Override
	public void onUpPressed() {
		Intent intent = NavUtils.getParentActivityIntent(this);
		intent.putExtra("tag", MainActivity.TAG_SUBSCRIPTIONS);
		NavUtils.navigateUpTo(this, intent);
	}

	private class LoadPopularListTask extends LoadGpodderListTask {

		public LoadPopularListTask(String url) {
			super(AddSubscriptionActivity.this, url, imageLoader, options,
					new SetAdapterCallback() {
						@Override
						public void setAdapter(ListAdapter adapter) {
							popularList.setAdapter(adapter);
							setLoading(false);
						}
					});

			setLoading(true);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> list, View view, int position,
			long id) {
		@SuppressWarnings("unchecked")
		HashMap<String, String> row = (HashMap<String, String>) list
				.getItemAtPosition(position);
		Intent intent = new Intent(this, DiscoverDetailActivity.class);
		intent.putExtra(GpodderJsonReader.KEY_TITLE,
				row.get(GpodderJsonReader.KEY_TITLE));
		intent.putExtra(GpodderJsonReader.KEY_DESCRIPTION,
				row.get(GpodderJsonReader.KEY_DESCRIPTION));
		intent.putExtra(GpodderJsonReader.KEY_SCALED_LOGO,
				row.get(GpodderJsonReader.KEY_SCALED_LOGO));
		intent.putExtra(GpodderJsonReader.KEY_URL,
				row.get(GpodderJsonReader.KEY_URL));
		intent.putExtra(GpodderJsonReader.KEY_WEBSITE,
				row.get(GpodderJsonReader.KEY_WEBSITE));
		startActivity(intent);
	}

	private String getUrlString(String input) {
		try {
			URL url = new URL(input);
			return url.toString();
		} catch (MalformedURLException e) {
			Matcher matcher = Pattern.compile(
					"\\b(http|https):[/]*[\\w-]+\\.[\\w./?&@#-]+").matcher(
					input);
			if (matcher.find()) {
				return matcher.group();
			}
		}
		return null;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (v.getId() == R.id.entry_search) {
			if (hasFocus) {
				showButton();
			} else {
				hideButton();
			}
		}
	}

	private void hideButton() {
		LayoutParams params = searchButton.getLayoutParams();
		params.width = 0;
		searchButton.setLayoutParams(params);
	}

	private void showButton() {
		LayoutParams params = searchButton.getLayoutParams();
		params.width = LayoutParams.WRAP_CONTENT;
		searchButton.setLayoutParams(params);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			submitSearch();
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_search) {
			submitSearch();
		}
	}

	private void submitSearch() {
		String query = searchEntry.getText().toString();
		String url = getUrlString(query);
		if (url != null) {
			Toast.makeText(this, R.string.message_subscribing_podcast,
					Toast.LENGTH_SHORT).show();
			new AddFeedTask(getApplicationContext()).executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, url);
			finish();
		} else {
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra("query", query);
			startActivity(intent);
		}
	}

	private void setLoading(boolean loading) {
		if (loading) {
			loadingIndicator.setVisibility(View.VISIBLE);
			popularList.setVisibility(View.GONE);
		} else {
			loadingIndicator.setVisibility(View.GONE);
			popularList.setVisibility(View.VISIBLE);

		}
	}

	private String getToplistUrl() {
		return "http://gpodder.net/toplist/100.json?scale_logo="
				+ Utils.dpToPx(AddSubscriptionActivity.this, 64);
	}

	private String getPopularForTagUrl(String tag) {

		try {
			return "http://gpodder.net/api/2/tag/"
					+ URLEncoder.encode(tag, "UTF-8") + "/100.json?scale_logo="
					+ Utils.dpToPx(AddSubscriptionActivity.this, 64);
		} catch (UnsupportedEncodingException e) {
			// should not happen
			return null;
		}
	}

	private class LoadTagsTask extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			Downloader downloader = new Downloader(AddSubscriptionActivity.this);
			try {
				HttpURLConnection connection = downloader
						.getConnection("https://gpodder.net/api/2/tags/40.json");
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(connection.getInputStream()));
					JsonReader json = new JsonReader(reader);
					json.beginArray();

					while (json.hasNext()) {
						json.beginObject();
						while (json.hasNext()) {

							String name = json.nextName();
							if (name.equals("tag")) {
								publishProgress(json.nextString());
							} else {
								json.skipValue();
							}
						}
						json.endObject();
					}
					json.endArray();
					json.close();
				} else {
					// handle failure TODO
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... progress) {
			tagAdapter.addAll(progress);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> spinner, View view, int position,
			long id) {
		if (loadTask.getStatus() != AsyncTask.Status.FINISHED) {
			loadTask.cancel(true);
		}
		String item = (String) spinner.getItemAtPosition(position);
		String url;
		if (item.equals(getString(R.string.title_tag_all))) {
			url = getToplistUrl();
		} else {
			url = getPopularForTagUrl(item);
		}
		loadTask = new LoadPopularListTask(url);
		loadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void onNothingSelected(AdapterView<?> spinner) {
	}

}
