package net.x4a42.volksempfaenger.ui;

import java.io.File;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class PodcastLogoView extends ImageView {
	private long podcastId;
	private VolksempfaengerApplication application;
	public final static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory().showImageForEmptyUri(R.drawable.default_logo)
			.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
			.displayer(new FadeInBitmapDisplayer(150)).build();

	public PodcastLogoView(Context context) {
		super(context);
		init(context);
	}

	public PodcastLogoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PodcastLogoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		if (isInEditMode()) {
			setImageResource(R.drawable.default_logo);
		} else {
			application = (VolksempfaengerApplication) context
					.getApplicationContext();
			podcastId = -1;
		}

	}

	public void reset() {
		setPodcastId(-1);
	}

	public void setPodcastId(long id) {
		setPodcastId(id, options);
	}

	public void setPodcastId(long id, DisplayImageOptions options) {
		if (id != podcastId) {
			podcastId = id;
			loadImage(options);
		}
	}

	private void loadImage(DisplayImageOptions options) {
		if (podcastId == -1) {
			setImageBitmap(null);
			return;
		}
		File podcastLogoFile = Utils
				.getPodcastLogoFile(getContext(), podcastId);
		String url = null;
		if (podcastLogoFile.exists()) {
			url = podcastLogoFile.toURI().toString();
		}

		application.imageLoader.displayImage(url, this, options);

	}
}
