package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
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
			.showStubImage(R.drawable.default_logo)
			.showImageForEmptyUri(R.drawable.default_logo).cacheInMemory()
			.cacheOnDisc().imageScaleType(ImageScaleType.POWER_OF_2).build();

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
		application = (VolksempfaengerApplication) context
				.getApplicationContext();
		podcastId = -1;
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
		String url = "";
		if (podcastId != -1) {
			url = Utils.getPodcastLogoFile(getContext(), podcastId).toURI()
					.toString();
		}
		application.imageLoader.displayImage(url, this, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(Bitmap loadedImage) {
						Animation animation = AnimationUtils.loadAnimation(
								getContext(), android.R.anim.fade_in);
						setAnimation(animation);
						animation.start();
					}
				});

	}
}
