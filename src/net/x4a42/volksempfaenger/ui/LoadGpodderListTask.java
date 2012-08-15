package net.x4a42.volksempfaenger.ui;

import java.util.ArrayList;
import java.util.HashMap;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.feedparser.GpodderJsonReader;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class LoadGpodderListTask extends
		AsyncTask<Void, HashMap<String, String>, Void> {

	private final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	private boolean first = true;
	private SimpleAdapter listAdapter;
	private final SetAdapterCallback callback;
	private final Context context;
	private final ImageLoader imageLoader;
	private final DisplayImageOptions options;

	public LoadGpodderListTask(Context context, ImageLoader imageLoader,
			DisplayImageOptions options, SetAdapterCallback callback) {
		super();
		this.callback = callback;
		this.context = context;
		this.imageLoader = imageLoader;
		this.options = options;
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
}