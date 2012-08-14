package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class DiscoverDetailActivity extends Activity implements
		OnUpPressedCallback {
	private final static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.default_logo)
			.showImageForEmptyUri(R.drawable.default_logo).cacheInMemory()
			.imageScaleType(ImageScaleType.POWER_OF_2).build();
	private ImageLoader imageLoader;
	private String feedUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discover_detail);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		imageLoader = ((VolksempfaengerApplication) getApplication()).imageLoader;

		TextView titleView = (TextView) findViewById(R.id.podcast_title);
		TextView descriptionView = (TextView) findViewById(R.id.podcast_description);
		ImageView logoView = (ImageView) findViewById(R.id.podcast_logo);

		Intent intent = getIntent();
		String title = intent.getStringExtra(DiscoverFragment.KEY_NAME);
		String description = intent
				.getStringExtra(DiscoverFragment.KEY_DESCRIPTION);
		String logo = intent.getStringExtra(DiscoverFragment.KEY_THUMBNAIL_URL);
		feedUrl = intent.getStringExtra(DiscoverFragment.KEY_URL);

		actionBar.setTitle(title);
		titleView.setText(title);
		descriptionView.setText(description);
		imageLoader.displayImage(logo, logoView, options);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_add:
			new AddFeedTask(getApplicationContext()).execute(feedUrl);
			return true;
		default:
			return ActivityHelper.handleGlobalMenu(this, item);
		}
	}

	@Override
	public void onUpPressed() {
		Intent intent = NavUtils
				.getParentActivityIntent(DiscoverDetailActivity.this);
		intent.putExtra("tag", MainActivity.discoverTag);
		NavUtils.navigateUpTo(DiscoverDetailActivity.this, intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.discover_detail, menu);
		ActivityHelper.addGlobalMenu(this, menu);
		return true;
	}

}
