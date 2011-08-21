package net.x4a42.volksempfaenger.ui;

import java.io.IOException;
import java.io.InputStreamReader;

import org.xmlpull.v1.XmlPullParserException;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.feedparser.Enclosure;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedItem;
import net.x4a42.volksempfaenger.feedparser.FeedParser;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class VolksempfaengerActivity extends BaseActivity implements
		OnClickListener {
	private Button buttonAddSubscription;
	private Button buttonSubscriptionList;
	private Button buttonListenQueue;
	private Button buttonDownloadQueue;
	private Button buttonSettings;
	private Button buttonTestFeed;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		buttonSubscriptionList = (Button) findViewById(R.id.button_subscriptionlist);
		buttonListenQueue = (Button) findViewById(R.id.button_listenqueue);
		buttonDownloadQueue = (Button) findViewById(R.id.button_downloadqueue);
		buttonTestFeed = (Button) findViewById(R.id.button_testfeed);

		buttonSubscriptionList.setOnClickListener(this);
		buttonListenQueue.setOnClickListener(this);
		buttonDownloadQueue.setOnClickListener(this);
		buttonTestFeed.setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent intent;

		switch (v.getId()) {
		case R.id.button_subscriptionlist:
			intent = new Intent(this, SubscriptionListActivity.class);
			startActivity(intent);
			break;
		case R.id.button_listenqueue:
			intent = new Intent(this, ListenQueueActivity.class);
			startActivity(intent);
			break;
		case R.id.button_downloadqueue:
			intent = new Intent(this, DownloadQueueActivity.class);
			startActivity(intent);
			break;
		case R.id.button_testfeed:
			Toast.makeText(this, "Read the logcat", Toast.LENGTH_SHORT).show();
			testFeedParser();
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		addGlobalMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		default:
			handleGlobalMenu(item);
		}
		return true;
	}

	public void testFeedParser() {
		try {
			Feed feed = FeedParser.parse(new InputStreamReader(getResources()
					.openRawResource(R.raw.rss2_test)));
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
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
