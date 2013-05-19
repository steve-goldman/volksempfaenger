package net.x4a42.volksempfaenger.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.feedparser.GpodderJsonReader;
import net.x4a42.volksempfaenger.feedparser.GpodderJsonReaderListener;
import net.x4a42.volksempfaenger.net.Downloader;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class LoadGpodderListTask extends
		AsyncTask<Void, HashMap<String, String>, Boolean> {

	private final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	private boolean first = true;
	private SimpleAdapter listAdapter;
	private final SetAdapterCallback callback;
	private final Context context;
	private final ImageLoader imageLoader;
	private final DisplayImageOptions options;
	private String url;

	public LoadGpodderListTask(Context context, String url,
			ImageLoader imageLoader, DisplayImageOptions options,
			SetAdapterCallback callback) {
		super();
		this.callback = callback;
		this.context = context;
		this.imageLoader = imageLoader;
		this.options = options;
		this.url = url;
	}

	@Override
	protected void onProgressUpdate(HashMap<String, String>... rows) {
		if (first) {
			first = false;
			listAdapter = new SimpleAdapter(context, list,
					R.layout.discover_list_row, new String[] {
							GpodderJsonReader.KEY_TITLE,
							GpodderJsonReader.KEY_SCALED_LOGO }, new int[] {
							R.id.podcast_name, R.id.podcast_logo });

			listAdapter.setViewBinder(new ViewBinder() {

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
			callback.setAdapter(listAdapter);
		}
		list.add(rows[0]);
		listAdapter.notifyDataSetChanged();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Downloader downloader = new Downloader(context);
		try {
			HttpURLConnection connection = downloader.getConnection(url);
			try {
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(connection.getInputStream()));
					new GpodderJsonReader(reader,
							new GpodderJsonReaderListener() {

								@SuppressWarnings("unchecked")
								@Override
								public void onPodcast(
										HashMap<String, String> podcast) {
									publishProgress(podcast);
								}
							}).read();
					return true;
				} else {
					return false;
				}
			} finally {
				connection.disconnect();
			}
		} catch (IOException e) {
			return false;
		}
	}
}