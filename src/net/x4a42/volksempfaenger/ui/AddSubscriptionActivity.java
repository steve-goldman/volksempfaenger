package net.x4a42.volksempfaenger.ui;

import java.io.BufferedReader;
import java.io.IOException;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.DbHelper;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedParser;
import net.x4a42.volksempfaenger.net.FeedDownloader;
import net.x4a42.volksempfaenger.net.NetException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddSubscriptionActivity extends BaseActivity implements
		OnClickListener {
	public static final int RESULT_SUCCEEDED = 0;
	public static final int RESULT_DOWNLOAD_FAILED = 1;
	public static final int RESULT_XML_EXCEPTION = 2;
	public static final int RESULT_IO_EXCEPTION = 3;
	public static final int RESULT_DUPLICATE = 4;

	private EditText editTextUrl;
	private Button buttonAdd;
	private Button buttonCancel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_subscription);

		LayoutParams params = getWindow().getAttributes();
		params.width = LayoutParams.FILL_PARENT;
		// params.flags = LayoutParams.FLAG_BLUR_BEHIND;
		getWindow().setAttributes(params);

		editTextUrl = (EditText) findViewById(R.id.edittext_url);
		buttonAdd = (Button) findViewById(R.id.button_add);
		buttonCancel = (Button) findViewById(R.id.button_cancel);

		buttonAdd.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_add:
			subscribeToFeed();
			break;
		case R.id.button_cancel:
			finish();
			break;
		}
	}

	private void subscribeToFeed() {
		String feedUrl = editTextUrl.getText().toString();

		new AddFeedTask().execute(feedUrl);
	}

	public class AddFeedTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			String feedUrl = params[0];
			FeedDownloader fd = new FeedDownloader(AddSubscriptionActivity.this);
			Feed feed;
			try {
				BufferedReader rd = fd.fetchFeed(feedUrl);
				feed = FeedParser.parse(rd);
			} catch (NetException e) {
				Log.i(getClass().getSimpleName(), "Exception handled", e);
				return RESULT_DOWNLOAD_FAILED;
			} catch (XmlPullParserException e) {
				Log.i(getClass().getSimpleName(), "Exception handled", e);
				return RESULT_XML_EXCEPTION;
			} catch (IOException e) {
				Log.i(getClass().getSimpleName(), "Exception handled", e);
				return RESULT_IO_EXCEPTION;
			}

			// Open database
			DbHelper dbHelper = new DbHelper(AddSubscriptionActivity.this);
			SQLiteDatabase db = dbHelper.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(DbHelper.Podcast.TITLE, feed.getTitle());
			values.put(DbHelper.Podcast.DESCRIPTION, feed.getDescription());
			values.put(DbHelper.Podcast.URL, feedUrl);
			values.put(DbHelper.Podcast.WEBSITE, feed.getWebsite());

			try {
				// Try to add the podcast to the database
				db.insertOrThrow(DbHelper.Podcast._TABLE, null, values);
				// Succeeded. Display message and exit
				return RESULT_SUCCEEDED;
			} catch (SQLException e) {
				// Something failed
				if (e instanceof SQLiteConstraintException) {
					// UNIQUE contraint on column url failed
					return RESULT_DUPLICATE;
				} else {
					// Some terrible failure happended
					Log.wtf(getClass().getName(), e);
				}
			} finally {
				dbHelper.close();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub

			String message = null;

			switch (result) {
			case RESULT_SUCCEEDED:
				Toast.makeText(AddSubscriptionActivity.this,
						R.string.message_podcast_successfully_added,
						Toast.LENGTH_SHORT).show();
				finish();
				return;
			case RESULT_DOWNLOAD_FAILED:
				message = getString(R.string.message_podcast_feed_download_failed);
				break;
			case RESULT_XML_EXCEPTION:
				message = getString(R.string.message_podcast_feed_parsing_failed);
				break;
			case RESULT_IO_EXCEPTION:
				message = getString(R.string.message_podcast_feed_io_exception);
				break;
			case RESULT_DUPLICATE:
				message = getString(R.string.message_podcast_already_added);
				break;
			}

			if (message != null) {
				Toast.makeText(AddSubscriptionActivity.this, message,
						Toast.LENGTH_SHORT).show();
			}
		}

	}
}
