package net.x4a42.volksempfaenger.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.feedparser.Enclosure;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedItem;
import net.x4a42.volksempfaenger.feedparser.FeedParser;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.net.EnclosureDownloader;
import net.x4a42.volksempfaenger.net.FeedDownloader;
import net.x4a42.volksempfaenger.net.NetException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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
	private Button buttonTestHttp;
	private Button buttonTestEncdl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		buttonSubscriptionList = (Button) findViewById(R.id.button_subscriptionlist);
		buttonListenQueue = (Button) findViewById(R.id.button_listenqueue);
		buttonDownloadQueue = (Button) findViewById(R.id.button_downloadqueue);
		buttonTestFeed = (Button) findViewById(R.id.button_testfeed);
		buttonTestHttp = (Button) findViewById(R.id.button_testhttp);
		buttonTestEncdl = (Button) findViewById(R.id.button_testencdl);

		buttonSubscriptionList.setOnClickListener(this);
		buttonListenQueue.setOnClickListener(this);
		buttonDownloadQueue.setOnClickListener(this);
		buttonTestFeed.setOnClickListener(this);
		buttonTestHttp.setOnClickListener(this);
		buttonTestEncdl.setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent intent;

		switch (v.getId()) {
		case R.id.button_subscriptionlist:
			intent = new Intent(this, SubscriptionListActivity.class);
			startActivity(intent);
			return;
		case R.id.button_listenqueue:
			intent = new Intent(this, ListenQueueActivity.class);
			startActivity(intent);
			return;
		case R.id.button_downloadqueue:
			intent = new Intent(this, DownloadQueueActivity.class);
			startActivity(intent);
			return;
		case R.id.button_testfeed:
			Toast.makeText(this, "Read the logcat", Toast.LENGTH_SHORT).show();
			testFeedParser();
			return;
		case R.id.button_testhttp:
			testFeedDownloader();
			return;
		case R.id.button_testencdl:
			testEnclosureDownloader();
			return;
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
					.openRawResource(R.raw.rss2_ns_test)));
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FeedParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testFeedDownloader() {
		new AsyncTask<String, String, String>() {
			@Override
			protected String doInBackground(String... params) {
				FeedDownloader d = new FeedDownloader(
						VolksempfaengerActivity.this);
				try {
					BufferedReader rd = d.fetchFeed(params[0]);
					String line;
					try {
						while ((line = rd.readLine()) != null) {
							publishProgress(line);
						}
					} catch (IOException e) {
						publishProgress(e.getMessage());
					}
				} catch (NetException e) {
					publishProgress(e.getMessage());
				}
				return null;
			}

			@Override
			protected void onProgressUpdate(String... values) {
				Toast.makeText(VolksempfaengerActivity.this, values[0],
						Toast.LENGTH_SHORT).show();
			}
		}.execute("http://vschuessler.org/");
	}

	public void testEnclosureDownloader() {
		String url = "http://upload.wikimedia.org/wikipedia/commons/3/3c/Podcastlogo.jpg";
		EnclosureDownloader d = new EnclosureDownloader(this);
		d.downloadEnclosure(url);
		Toast.makeText(this, String.format("Queued %s", url),
				Toast.LENGTH_SHORT).show();
	}
}
