package net.x4a42.volksempfaenger.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.feedparser.GpodderJsonReader;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SearchActivity extends Activity implements OnUpPressedCallback,
		OnItemClickListener, OnClickListener {
	private ImageLoader imageLoader;
	private final static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.default_logo)
			.showImageForEmptyUri(R.drawable.default_logo).cacheInMemory()
			.cacheOnDisc().build();
	private LoadSearchTask searchTask;
	private ListView resultsList;
	private View loadingIndicator;
	private View loadingErrorIndicator;
	private Button retryButton;
	private String searchQuery;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.title_search_results));

		setContentView(R.layout.search_results);

		resultsList = (ListView) findViewById(R.id.results_list);
		resultsList.setOnItemClickListener(this);

		TextView header = (TextView) getLayoutInflater().inflate(
				R.layout.search_results_header, resultsList, false);
		resultsList.addHeaderView(header);

		loadingIndicator = findViewById(R.id.loading);
		loadingErrorIndicator = findViewById(R.id.loading_error);

		retryButton = (Button) findViewById(R.id.button_retry);
		retryButton.setOnClickListener(this);

		imageLoader = ((VolksempfaengerApplication) getApplication()).imageLoader;

		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		searchQuery = intent.getStringExtra("query");
		header.setText(String.format(
				getString(R.string.title_search_results_for), searchQuery));
		onSearch();
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
		NavUtils.navigateUpFromSameTask(this);
	}

	private void onSearch() {
		setLoading(true);
		if (searchTask != null
				&& searchTask.getStatus() != AsyncTask.Status.FINISHED) {
			searchTask.cancel(true);
		}
		searchTask = new LoadSearchTask(searchQuery);
		searchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private class LoadSearchTask extends LoadGpodderListTask {

		public LoadSearchTask(String query) {
			super(SearchActivity.this, buildUrl(query), imageLoader, options,
					new SetAdapterCallback() {
						@Override
						public void setAdapter(ListAdapter adapter) {
							resultsList.setAdapter(adapter);
							setLoading(false);
						}
					});
		}

		@Override
		protected void onPostExecute(Boolean successful) {
			if (!successful) {
				loadingIndicator.setVisibility(View.GONE);
				resultsList.setVisibility(View.GONE);
				loadingErrorIndicator.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> list, View view, int position,
			long id) {
		@SuppressWarnings("unchecked")
		HashMap<String, String> row = (HashMap<String, String>) list
				.getItemAtPosition(position);
		if (row == null) {
			return;
		}
		Intent intent = new Intent(this, DiscoverDetailActivity.class)
				.putExtra(GpodderJsonReader.KEY_TITLE,
						row.get(GpodderJsonReader.KEY_TITLE))
				.putExtra(GpodderJsonReader.KEY_DESCRIPTION,
						row.get(GpodderJsonReader.KEY_DESCRIPTION))
				.putExtra(GpodderJsonReader.KEY_SCALED_LOGO,
						row.get(GpodderJsonReader.KEY_SCALED_LOGO))
				.putExtra(GpodderJsonReader.KEY_URL,
						row.get(GpodderJsonReader.KEY_URL))
				.putExtra(GpodderJsonReader.KEY_WEBSITE,
						row.get(GpodderJsonReader.KEY_WEBSITE));
		startActivity(intent);
	}

	private String buildUrl(String query) {
		try {
			return "http://gpodder.net/search.json?scale_logo="
					+ Utils.dpToPx(this, 64) + "&q="
					+ URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// should not happen
			return null;
		}
	}

	private void setLoading(boolean loading) {
		loadingErrorIndicator.setVisibility(View.GONE);
		if (loading) {
			loadingIndicator.setVisibility(View.VISIBLE);
			resultsList.setVisibility(View.GONE);
		} else {
			loadingIndicator.setVisibility(View.GONE);
			resultsList.setVisibility(View.VISIBLE);

		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_retry) {
			onSearch();
		}
	}
}
