package net.x4a42.volksempfaenger.ui;

import java.io.IOException;
import java.io.InputStreamReader;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.StorageException;
import net.x4a42.volksempfaenger.feedparser.Enclosure;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedItem;
import net.x4a42.volksempfaenger.feedparser.FeedParser;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.net.EnclosureDownloader;
import net.x4a42.volksempfaenger.net.LogoDownloader;
import net.x4a42.volksempfaenger.net.NetException;
import net.x4a42.volksempfaenger.service.UpdateService;
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
	private Button buttonTestEncdl;
	private Button buttonViewEpisode;
	private Button buttonStartUpdate;
	private Button buttonLogoDownloader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		buttonSubscriptionList = (Button) findViewById(R.id.button_subscriptionlist);
		buttonListenQueue = (Button) findViewById(R.id.button_listenqueue);
		buttonDownloadQueue = (Button) findViewById(R.id.button_downloadqueue);
		buttonTestFeed = (Button) findViewById(R.id.button_testfeed);
		buttonTestEncdl = (Button) findViewById(R.id.button_testencdl);
		buttonViewEpisode = (Button) findViewById(R.id.button_viewepisode);
		buttonStartUpdate = (Button) findViewById(R.id.button_startupdate);
		buttonLogoDownloader = (Button) findViewById(R.id.button_logodown);

		buttonSubscriptionList.setOnClickListener(this);
		buttonListenQueue.setOnClickListener(this);
		buttonDownloadQueue.setOnClickListener(this);
		buttonTestFeed.setOnClickListener(this);
		buttonTestEncdl.setOnClickListener(this);
		buttonViewEpisode.setOnClickListener(this);
		buttonStartUpdate.setOnClickListener(this);
		buttonLogoDownloader.setOnClickListener(this);
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
		case R.id.button_testencdl:
			testEnclosureDownloader();
			return;
		case R.id.button_viewepisode:
			intent = new Intent(this, ViewEpisodeActivity.class);
			startActivity(intent);
			return;
		case R.id.button_startupdate:
			intent = new Intent(this, UpdateService.class);
			startService(intent);
			return;
		case R.id.button_logodown:
			new AsyncTask<String, Void, Boolean>() {

				@Override
				protected void onPreExecute() {
					Toast.makeText(VolksempfaengerActivity.this,
							"Download started", Toast.LENGTH_SHORT).show();
				}

				@Override
				protected Boolean doInBackground(String... params) {
					LogoDownloader ld = new LogoDownloader(
							VolksempfaengerActivity.this);
					try {
						ld.fetchLogo(params[0], 42);
						return true;
					} catch (NetException e) {
						return false;
					} catch (StorageException e) {
						return false;
					}
				}

				@Override
				protected void onPostExecute(Boolean result) {
					if (result) {
						Toast.makeText(VolksempfaengerActivity.this,
								"Download finished", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(VolksempfaengerActivity.this,
								"Download failed", Toast.LENGTH_SHORT).show();
					}
				}

			}.execute("http://upload.wikimedia.org/wikipedia/commons/3/3c/Podcastlogo.jpg");
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
					.openRawResource(R.raw.atom_test)));
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FeedParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testEnclosureDownloader() {
		String url = "http://upload.wikimedia.org/wikipedia/commons/3/3c/Podcastlogo.jpg";
		EnclosureDownloader d = new EnclosureDownloader(this);
		d.downloadEnclosure(url);
		Toast.makeText(this, String.format("Queued %s", url),
				Toast.LENGTH_SHORT).show();
	}
}
