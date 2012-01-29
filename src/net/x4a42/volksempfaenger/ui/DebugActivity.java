package net.x4a42.volksempfaenger.ui;

import java.io.File;
import java.io.FileReader;

import net.x4a42.volksempfaenger.Constants;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.feedparser.Enclosure;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedItem;
import net.x4a42.volksempfaenger.feedparser.FeedParser;
import net.x4a42.volksempfaenger.service.CleanCacheService;
import net.x4a42.volksempfaenger.service.DownloadService;
import net.x4a42.volksempfaenger.service.UpdateService;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DebugActivity extends Activity implements OnClickListener {

	private static final String TAG = "DebugActivity";
	private static final int REQUEST_PICK_FEED = 0;

	private Button buttonStartUpdate;
	private Button buttonStartDownload;
	private Button buttonStartClean;
	private Button buttonTestFeed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug);

		buttonStartUpdate = (Button) findViewById(R.id.button_startupdate);
		buttonStartDownload = (Button) findViewById(R.id.button_startdownload);
		buttonStartClean = (Button) findViewById(R.id.button_startclean);
		buttonTestFeed = (Button) findViewById(R.id.button_testfeed);

		buttonStartUpdate.setOnClickListener(this);
		buttonStartDownload.setOnClickListener(this);
		buttonStartClean.setOnClickListener(this);
		buttonTestFeed.setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent intent;

		switch (v.getId()) {
		case R.id.button_testfeed:
			intent = new Intent(Constants.ACTION_OI_PICK_FILE);
			intent.setData(Uri.fromFile(new File(getExternalFilesDir(null)
					.getParent(), "debug")));
			intent.putExtra(Constants.EXTRA_OI_TITLE, "Select feed");
			startActivityForResult(intent, REQUEST_PICK_FEED);
			// testFeedParser();
			return;
		case R.id.button_startupdate:
			intent = new Intent(this, UpdateService.class);
			startService(intent);
			return;
		case R.id.button_startclean:
			intent = new Intent(this, CleanCacheService.class);
			startService(intent);
			return;
		case R.id.button_startdownload:
			intent = new Intent(this, DownloadService.class);
			startService(intent);
			return;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_PICK_FEED:
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					File file = new File(data.getData().getPath());
					testFeedParser(file);
				}
			}
			break;
		}
	}

	public void testFeedParser(File file) {
		Toast.makeText(this, "Read the logcat", Toast.LENGTH_SHORT).show();
		try {
			Feed feed = FeedParser.parse(new FileReader(file));
			Log.d(TAG, "Title: " + feed.getTitle());
			if (feed.getUrl() != null) {
				Log.d(TAG, "URL: " + feed.getUrl());
			}
			if (feed.getWebsite() != null) {
				Log.d(TAG, "Website: " + feed.getWebsite());
			}
			if (feed.getDescription() != null) {
				Log.d(TAG, "Description: " + feed.getDescription());
			}

			for (FeedItem item : feed.getItems()) {
				Log.d(TAG, "Item title: " + item.getTitle());
				Log.d(TAG, "Item ID: " + item.getItemId());
				if (item.getDate() != null) {
					Log.d(TAG, item.getDate().toString());
				}
				if (item.getUrl() != null) {
					Log.d(TAG, item.getUrl());
				}
				if (item.getDescription() != null) {
					Log.d(TAG, item.getDescription());
				}
				for (Enclosure enc : item.getEnclosures()) {
					Log.d(TAG, "Enclosure");
					if (enc.getTitle() != null) {
						Log.d(TAG, enc.getTitle());
					}
					if (enc.getUrl() != null) {
						Log.d(TAG, enc.getUrl());
					}
					if (enc.getMime() != null) {
						Log.d(TAG, enc.getMime());
					}
					if (enc.getSize() != 0) {
						Log.d(TAG,
								"Size: " + (new Long(enc.getSize())).toString());
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "An error occurred while parsing the feed:", e);
		}
	}

}
