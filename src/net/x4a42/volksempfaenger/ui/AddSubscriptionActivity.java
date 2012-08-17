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
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class AddSubscriptionActivity extends Activity implements
		OnUpPressedCallback, OnItemClickListener, OnQueryTextListener {

	private ImageLoader imageLoader;
	private final static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.default_logo)
			.showImageForEmptyUri(R.drawable.default_logo).cacheInMemory()
			.cacheOnDisc().imageScaleType(ImageScaleType.POWER_OF_2).build();
	private File toplistFile;
	private AsyncTask<Void, HashMap<String, String>, Void> loadTask;
	private ListView popularList;
	private MenuItem searchMenuItem;
	private String query;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.title_add_subscription);
		setContentView(R.layout.add_subscription);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		popularList = (ListView) findViewById(R.id.popular_list);
		popularList.setOnItemClickListener(this);

		View header_text = (View) getLayoutInflater().inflate(
				R.layout.add_subscription_header_text, popularList, false);
		View header_title = (View) getLayoutInflater().inflate(
				R.layout.add_subscription_header_title, popularList, false);
		popularList.addHeaderView(header_text, null, false);
		popularList.addHeaderView(header_title, null, false);

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
			query = data.toString();
		} else {
			// see if there is a link in the clipboard
			ClipboardManager cm = (ClipboardManager) getSystemService(Activity.CLIPBOARD_SERVICE);
			ClipData clip = cm.getPrimaryClip();
			if (clip != null) {
				ClipData.Item item = clip.getItemAt(0);
				if (item != null && item.getText() != null) {
					// return value may be null
					query = getUrlString(item.getText().toString());
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.discover, menu);
		searchMenuItem = menu.findItem(R.id.menu_search);

		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(new ComponentName(getPackageName(),
						SearchActivity.class.getName())));
		searchView.setOnQueryTextListener(this);
		if (query != null) {
			searchMenuItem.expandActionView();
			searchView.setQuery(query, false);
		}
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
		intent.putExtra("tag", MainActivity.subscriptionsTag);
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
												64), tempFile);
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

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			if (searchMenuItem != null) {
				searchMenuItem.expandActionView();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		String url = getUrlString(query);
		if (url != null) {
			Toast.makeText(this, R.string.message_subscribing_podcast,
					Toast.LENGTH_SHORT).show();
			new AddFeedTask(getApplicationContext()).execute(url);
			finish();
			return true;
		}
		return false;
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

}
