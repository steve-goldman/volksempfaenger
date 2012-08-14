package net.x4a42.volksempfaenger.ui;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.misc.StorageException;
import net.x4a42.volksempfaenger.net.FileDownloader;
import net.x4a42.volksempfaenger.net.NetException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class DiscoverFragment extends ListFragment implements
		OnItemClickListener {

	public final static String KEY_NAME = "title";
	public final static String KEY_URL = "url";
	public final static String KEY_DESCRIPTION = "description";
	public final static String KEY_WEBSITE_URL = "website";
	public final static String KEY_THUMBNAIL_URL = "scaled_logo_url";
	private ImageLoader imageLoader;
	private final static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.default_logo)
			.showImageForEmptyUri(R.drawable.default_logo).cacheInMemory()
			.imageScaleType(ImageScaleType.POWER_OF_2).build();
	private File toplistFile;
	private AsyncTask<Void, Void, Void> loadTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		toplistFile = new File(getActivity().getFilesDir(), "popular.json");

		imageLoader = ((VolksempfaengerApplication) getActivity()
				.getApplication()).imageLoader;
		if (toplistFile.exists()) {
			loadTask = new LoadPopularListTask()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		new RefreshPopularListTask()
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.discover, menu);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
	}

	private class LoadPopularListTask extends AsyncTask<Void, Void, Void> {

		private final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		@Override
		protected Void doInBackground(Void... params) {
			JSONArray feeds;
			try {
				StringWriter stringWriter;
				synchronized (toplistFile) {
					FileReader fileReader = new FileReader(toplistFile);
					stringWriter = new StringWriter();
					Utils.copy(fileReader, stringWriter);
					fileReader.close();
				}
				feeds = new JSONArray(stringWriter.toString());
			} catch (IOException e) {
				// TODO handle error
				return null;
			} catch (JSONException e) {
				// TODO handle error
				return null;
			}
			for (int i = 0; i < feeds.length(); i++) {
				try {
					JSONObject feed = feeds.getJSONObject(i);
					HashMap<String, String> feedMap = new HashMap<String, String>();
					if (feed.isNull(KEY_NAME) || feed.isNull(KEY_URL)) {
						continue;
					}
					feedMap.put(KEY_NAME, feed.getString(KEY_NAME));
					feedMap.put(KEY_URL, feed.getString(KEY_URL));
					feedMap.put(
							KEY_DESCRIPTION,
							feed.isNull(KEY_DESCRIPTION) ? "" : feed
									.getString(KEY_DESCRIPTION));
					feedMap.put(
							KEY_WEBSITE_URL,
							feed.isNull(KEY_WEBSITE_URL) ? "" : feed
									.getString(KEY_WEBSITE_URL));
					feedMap.put(
							KEY_THUMBNAIL_URL,
							feed.isNull(KEY_THUMBNAIL_URL) ? "" : feed
									.getString(KEY_THUMBNAIL_URL));
					list.add(feedMap);
				} catch (JSONException e) {
					// TODO handle error
					continue;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			SimpleAdapter adapter = new SimpleAdapter(getActivity(), list,
					R.layout.discover_list_row, new String[] { KEY_NAME,
							KEY_THUMBNAIL_URL }, new int[] { R.id.podcast_name,
							R.id.podcast_logo });
			adapter.setViewBinder(new ViewBinder() {

				@Override
				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					if (view.getId() == R.id.podcast_logo) {
						ImageView logoView = (ImageView) view;
						imageLoader.displayImage(textRepresentation, logoView,
								options);
						return true;
					} else {
						return false;
					}
				}
			});
			setListAdapter(adapter);
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
						getActivity().getCacheDir());
				FileDownloader downloader = new FileDownloader(getActivity());
				downloader.fetchFile(
						"http://gpodder.net/toplist/100.json?scale_logo="
								+ Utils.dpToPx(getActivity(), 64), tempFile);
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
		Intent intent = new Intent(getActivity(), DiscoverDetailActivity.class);
		intent.putExtra(KEY_NAME, row.get(KEY_NAME));
		intent.putExtra(KEY_DESCRIPTION, row.get(KEY_DESCRIPTION));
		intent.putExtra(KEY_THUMBNAIL_URL, row.get(KEY_THUMBNAIL_URL));
		intent.putExtra(KEY_URL, row.get(KEY_URL));
		intent.putExtra(KEY_WEBSITE_URL, row.get(KEY_WEBSITE_URL));
		startActivity(intent);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ListView list = (ListView) view.findViewById(android.R.id.list);
		list.setOnItemClickListener(this);
	}
}
