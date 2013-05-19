package net.x4a42.volksempfaenger.ui;

import java.io.File;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class PodcastLogoView extends ImageView {
	private long podcastId;
	private VolksempfaengerApplication application;
	private final static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory().showImageForEmptyUri(R.drawable.default_logo)
			.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).build();
	private Animation fadeInAnimation;

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
		fadeInAnimation = AnimationUtils.loadAnimation(getContext(),
				android.R.anim.fade_in);
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
		if (id != podcastId) {
			podcastId = id;
			loadImage();
		}
	}

	private void loadImage() {
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

		application.imageLoader.displayImage(url, this, options,
				new SimpleImageLoadingListener() {
					private long startTime;

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						startTime = System.currentTimeMillis();
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						if (System.currentTimeMillis() - startTime > 16) {
							setAnimation(fadeInAnimation);
							fadeInAnimation.start();
						}
					}
				});

	}
}
