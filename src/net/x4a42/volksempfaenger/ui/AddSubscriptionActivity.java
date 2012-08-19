package net.x4a42.volksempfaenger.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.feedparser.GpodderJsonReader;
import net.x4a42.volksempfaenger.feedparser.GpodderJsonReaderListener;
import net.x4a42.volksempfaenger.misc.StorageException;
import net.x4a42.volksempfaenger.net.FileDownloader;
import net.x4a42.volksempfaenger.net.NetException;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class AddSubscriptionActivity extends Activity implements
		OnUpPressedCallback, OnItemClickListener, OnFocusChangeListener,
		OnEditorActionListener, OnClickListener {

	private ImageLoader imageLoader;
	private final static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.default_logo)
			.showImageForEmptyUri(R.drawable.default_logo).cacheInMemory()
			.cacheOnDisc().imageScaleType(ImageScaleType.POWER_OF_2).build();
	private File toplistFile;
	private AsyncTask<Void, HashMap<String, String>, Void> loadTask;
	private ListView popularList;
	private AutoCompleteTextView searchEntry;
	private ImageButton searchButton;

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

		toplistFile = new File(getFilesDir(), "popular.json");

		imageLoader = ((VolksempfaengerApplication) getApplication()).imageLoader;
		if (toplistFile.exists()) {
			loadTask = new LoadPopularListTask()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		new RefreshPopularListTask()
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

		public LoadPopularListTask() {
			super(AddSubscriptionActivity.this, imageLoader, options,
					new SetAdapterCallback() {
						@Override
						public void setAdapter(ListAdapter adapter) {
							popularList.setAdapter(adapter);
						}
					});
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				synchronized (toplistFile) {
					BufferedReader fileReader = new BufferedReader(
							new FileReader(toplistFile));
					new GpodderJsonReader(fileReader,
							new GpodderJsonReaderListener() {

								@SuppressWarnings("unchecked")
								@Override
								public void onPodcast(
										HashMap<String, String> podcast) {
									publishProgress(podcast);
								}
							}).read();
				}
			} catch (IOException e) {
				// TODO handle error
				return null;
			}
			return null;
		}
	}

	private class RefreshPopularListTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				synchronized (toplistFile) {
					// 1 day
					final long minTime = System.currentTimeMillis() - 24 * 60
							* 60 * 1000;
					if (toplistFile.exists()
							&& toplistFile.lastModified() < minTime) {
						return false;
					}
				}
				File tempFile = File.createTempFile("popular", ".json",
						getCacheDir());
				FileDownloader downloader = new FileDownloader(
						AddSubscriptionActivity.this);
				downloader
						.fetchFile(
								"http://gpodder.net/toplist/100.json?scale_logo="
										+ Utils.dpToPx(
												AddSubscriptionActivity.this,
												64), null, tempFile);
				synchronized (toplistFile) {
					tempFile.renameTo(toplistFile);
				}
			} catch (NetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (StorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				return;
			}
			if (loadTask != null
					&& loadTask.getStatus() != AsyncTask.Status.FINISHED) {
				loadTask.cancel(true);
			}

			loadTask = new LoadPopularListTask()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
			new AddFeedTask(getApplicationContext()).execute(url);
			finish();
		} else {
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra("query", query);
			startActivity(intent);
		}
	}

}
