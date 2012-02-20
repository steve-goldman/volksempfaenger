package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.CacheMap;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PodcastLogoView extends ImageView {

	private static CacheMap<Long, Bitmap> cache = new CacheMap<Long, Bitmap>();

	private long podcastId;
	private AsyncTask<Void, Void, Bitmap> lastTask;

	public PodcastLogoView(Context context) {
		super(context);
		init();
	}

	public PodcastLogoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PodcastLogoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		setImageResource(R.drawable.default_logo);
	}

	public void setPodcastId(long id) {
		if (id == podcastId) {
			// don't reload the logo
			return;
		}

		if (lastTask != null) {
			lastTask.cancel(false);
		}

		Bitmap logo = cache.get(id);
		if (logo != null) {
			// the logo is cached, we don't need to fire off an AsyncTask
			setImageBitmap(logo);
		} else {
			lastTask = new LoadTask(id).execute();
		}

		podcastId = id;
	}

	public long getPodcastId() {
		return podcastId;
	}

	private class LoadTask extends AsyncTask<Void, Void, Bitmap> {

		private long id;

		private LoadTask(long id) {
			this.id = id;
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			Bitmap logo = Utils.getPodcastLogoBitmap(getContext(), id);
			if (logo != null) {
				cache.put(id, logo);
			}
			return logo;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				setImageBitmap(result);
			} else {
				setImageResource(R.drawable.default_logo);
			}
		}

		@Override
		protected void onCancelled(Bitmap result) {
			init();
		}

	}

}
