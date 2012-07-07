package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PodcastLogoView extends ImageView {
	private long podcastId;
	private AsyncTask<Void, Void, Bitmap> lastTask;
	private static Bitmap defaultLogo;

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
		podcastId = -1;
		resetImage();
	}

	private void resetImage() {
		if (defaultLogo == null) {
			defaultLogo = Utils.scalePodcastLogo(getContext(), BitmapFactory
					.decodeResource(getResources(), R.drawable.default_logo));
		}
		setImageBitmap(defaultLogo);
	}

	public void setPodcastId(long id) {
		if (id == podcastId) {
			// don't reload the logo
			return;
		}

		if (lastTask != null) {
			lastTask.cancel(false);
		}

		Bitmap logo = VolksempfaengerApplication.getCache().get(id);
		if (logo != null) {
			// the logo is cached, we don't need to fire off an AsyncTask
			setImageBitmap(logo);
		} else {
			Bitmap preview = VolksempfaengerApplication.getPreviewCache().get(
					id);
			if (preview != null) {
				setImageBitmap(preview);
			}
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
			} else {
				resetImage();
			}
		}

		@Override
		protected void onCancelled(Bitmap result) {
			resetImage();
		}

	}

}
