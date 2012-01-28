package net.x4a42.volksempfaenger.ui;

import java.io.IOException;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.feedparser.Enclosure;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedItem;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.service.CleanCacheService;
import net.x4a42.volksempfaenger.service.DownloadService;
import net.x4a42.volksempfaenger.service.UpdateService;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DebugActivity extends BaseActivity implements OnClickListener {

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
			Toast.makeText(this, "Read the logcat", Toast.LENGTH_SHORT).show();
			testFeedParser();
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

	public void testFeedParser() {
		try {
			// Feed feed = FeedParser.parse(new InputStreamReader(getResources()
			// .openRawResource(R.raw.atom_test)));
			// TODO read feed from sdcard
			Feed feed = null;
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
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (FeedParserException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

}
