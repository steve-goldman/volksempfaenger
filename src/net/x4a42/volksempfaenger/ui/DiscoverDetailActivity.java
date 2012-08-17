package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.feedparser.GpodderJsonReader;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
		Button websiteButton = (Button) findViewById(R.id.button_website);

		Intent intent = getIntent();
		String title = intent.getStringExtra(GpodderJsonReader.KEY_TITLE);
		String description = intent
				.getStringExtra(GpodderJsonReader.KEY_DESCRIPTION);
		String logo = intent.getStringExtra(GpodderJsonReader.KEY_SCALED_LOGO);
		feedUrl = intent.getStringExtra(GpodderJsonReader.KEY_URL);
		final String websiteUrl = intent
				.getStringExtra(GpodderJsonReader.KEY_WEBSITE);

		actionBar.setTitle(title);
		titleView.setText(title);
		descriptionView.setText(description);
		imageLoader.displayImage(logo, logoView, options);
		websiteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(websiteUrl));
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_add:
			Toast.makeText(this, R.string.message_subscribing_podcast,
					Toast.LENGTH_SHORT).show();
			new AddFeedTask(getApplicationContext()).execute(feedUrl);
			return true;
		default:
			return ActivityHelper.handleGlobalMenu(this, item);
		}
	}

	@Override
	public void onUpPressed() {
		NavUtils.navigateUpFromSameTask(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.discover_detail, menu);
		ActivityHelper.addGlobalMenu(this, menu);
		return true;
	}

}
