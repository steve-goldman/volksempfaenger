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
		reset();
	}

	public PodcastLogoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		reset();
	}

	public PodcastLogoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		reset();
	}

	public void reset() {
		reset(true);
	}

	private void reset(boolean resetId) {
		setImageResource(R.drawable.default_logo);
		if (resetId) {
			podcastId = -1;
		}
	}

	public void setPodcastId(long id) {
		if (id == podcastId) {
			// don't reload the logo
			return;
		}

		if (lastTask != null) {
			lastTask.cancel(false);
		} else {
			reset();
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
			return Utils.getPodcastLogoBitmap(getContext(), id);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				setImageBitmap(result);
				cache.put(id, result);
			} else {
				reset(false);
			}
		}

		@Override
		protected void onCancelled(Bitmap result) {
			reset(false);
			if (result != null) {
				cache.put(id, result);
			}
		}

	}

}
