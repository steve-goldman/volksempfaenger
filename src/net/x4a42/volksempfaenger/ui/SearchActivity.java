package net.x4a42.volksempfaenger.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.feedparser.GpodderJsonReader;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SearchActivity extends ListActivity implements OnUpPressedCallback {
	private ImageLoader imageLoader;
	private final static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.default_logo)
			.showImageForEmptyUri(R.drawable.default_logo).cacheInMemory()
			.cacheOnDisc().build();
	private LoadSearchTask searchTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.title_search_results));

		TextView header = (TextView) getLayoutInflater().inflate(
				R.layout.search_results_header, getListView(), false);
		getListView().addHeaderView(header);

		imageLoader = ((VolksempfaengerApplication) getApplication()).imageLoader;

		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		String query = intent.getStringExtra("query");
		header.setText(String.format(
				getString(R.string.title_search_results_for), query));
		onSearch(query);
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

	private void onSearch(String query) {
		if (searchTask != null
				&& searchTask.getStatus() != AsyncTask.Status.FINISHED) {
			searchTask.cancel(true);
		}
		searchTask = new LoadSearchTask(query);
		searchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private class LoadSearchTask extends LoadGpodderListTask {

		public LoadSearchTask(String query) {
			super(SearchActivity.this, buildUrl(query), imageLoader, options,
					new SetAdapterCallback() {
						@Override
						public void setAdapter(ListAdapter adapter) {
							setListAdapter(adapter);
						}
					});
		}
	}

	@Override
	public void onListItemClick(ListView list, View view, int position, long id) {
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
}
