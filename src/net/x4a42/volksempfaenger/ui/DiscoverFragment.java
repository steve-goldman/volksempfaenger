package net.x4a42.volksempfaenger.ui;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class DiscoverFragment extends ListFragment {

	private final static String KEY_NAME = "name";
	private final static String KEY_URL = "url";
	private final static String KEY_DESCRIPTION = "description";
	private final static String KEY_WEBSITE_URL = "website_url";
	private final static String KEY_THUMBNAIL_URL = "thumbnail_url";
	private ImageLoader imageLoader;
	private final static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.default_logo)
			.showImageForEmptyUri(R.drawable.default_logo).cacheInMemory()
			.cacheOnDisc().imageScaleType(ImageScaleType.POWER_OF_2).build();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		imageLoader = ImageLoader.getInstance();
		int maxSize = Utils.dpToPx(getActivity(), 64);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getActivity()).memoryCacheSize(1024 * 1024)
				.discCacheSize(1024 * 1024 * 5)
				.memoryCacheExtraOptions(maxSize, maxSize)
				.discCacheExtraOptions(maxSize, maxSize, null, 100).build();
		imageLoader.init(config);

		new LoadPopularListTask().execute();
	}

	private class LoadPopularListTask extends AsyncTask<Void, Void, Void> {

		private final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		@Override
		protected Void doInBackground(Void... params) {
			URL url;
			JSONArray feeds;
			try {
				url = new URL("http://vschuessler.org/popular.json");
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					// TODO handle error
				}
				InputStreamReader in = new InputStreamReader(
						urlConnection.getInputStream());
				int initalSize = urlConnection.getContentLength();
				initalSize = (initalSize < 0) ? 16 : initalSize;
				StringWriter stringWriter = new StringWriter(initalSize);
				Utils.copy(in, stringWriter);
				in.close();
				feeds = new JSONArray(stringWriter.toString());
			} catch (MalformedURLException e) {
				return null;
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
}
