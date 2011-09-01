package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.DatabaseHelper;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.net.FeedDownloader;
import net.x4a42.volksempfaenger.net.LogoDownloader;
import net.x4a42.volksempfaenger.net.NetException;
import net.x4a42.volksempfaenger.service.UpdateService;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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

	private DatabaseHelper dbHelper;
	private EditText editTextUrl;
	private Button buttonAdd;
	private Button buttonCancel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_subscription);

		LayoutParams params = getWindow().getAttributes();
		params.width = LayoutParams.FILL_PARENT;
		getWindow().setAttributes(params);

		editTextUrl = (EditText) findViewById(R.id.edittext_url);
		buttonAdd = (Button) findViewById(R.id.button_add);
		buttonCancel = (Button) findViewById(R.id.button_cancel);

		buttonAdd.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);

		dbHelper = DatabaseHelper.getInstance(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_add:
			subscribeToFeed();
			return;
		case R.id.button_cancel:
			finish();
			return;
		}
	}

	private void subscribeToFeed() {
		String feedUrl = editTextUrl.getText().toString();

		new AddFeedTask().execute(feedUrl);
	}

	public class AddFeedTask extends AsyncTask<String, Void, Integer> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(AddSubscriptionActivity.this,
					getString(R.string.dialog_add_progress_title),
					getString(R.string.dialog_add_progress_message), true);
		}

		@Override
		protected Integer doInBackground(String... params) {
			String feedUrl = params[0];
			FeedDownloader fd = new FeedDownloader(AddSubscriptionActivity.this);
			Feed feed;
			try {
				feed = fd.fetchFeed(feedUrl);
			} catch (NetException e) {
				Log.i(getClass().getSimpleName(), "Exception handled", e);
				return RESULT_DOWNLOAD_FAILED;
			} catch (FeedParserException e) {
				Log.i(getClass().getSimpleName(), "Exception handled", e);
				return RESULT_XML_EXCEPTION;
			}

			// Open database
			SQLiteDatabase db = dbHelper.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.Podcast.TITLE, feed.getTitle());
			values.put(DatabaseHelper.Podcast.DESCRIPTION,
					feed.getDescription());
			values.put(DatabaseHelper.Podcast.URL, feedUrl);
			values.put(DatabaseHelper.Podcast.WEBSITE, feed.getWebsite());

			try {
				// Try to add the podcast to the database
				long podcastId = db.insertOrThrow(
						DatabaseHelper.Podcast._TABLE, null, values);
				Intent updatePodcast = new Intent(AddSubscriptionActivity.this,
						UpdateService.class);
				updatePodcast.putExtra("id", new long[] { podcastId });
				updatePodcast.putExtra("first_sync", true);
				startService(updatePodcast);
				// Succeeded
				String feedImage = feed.getImage();
				if (feedImage != null) {
					// Try to download podcast logo
					LogoDownloader ld = new LogoDownloader(
							AddSubscriptionActivity.this);
					try {
						ld.fetchLogo(feedImage, podcastId);
					} catch (Exception e) {
						// Who cares?
					}
				}
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
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			dialog.dismiss();

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
				AlertDialog.Builder builder = new AlertDialog.Builder(
						AddSubscriptionActivity.this);
				builder.setTitle(R.string.dialog_error_title)
						.setMessage(message)
						.setCancelable(false)
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								})
						.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										AddSubscriptionActivity.this.finish();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}

		@Override
		protected void onCancelled() {
			dialog.dismiss();
		}

	}
}
