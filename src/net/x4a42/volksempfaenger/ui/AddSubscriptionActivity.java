package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.data.Error;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.net.FeedDownloader;
import net.x4a42.volksempfaenger.net.LogoDownloader;
import net.x4a42.volksempfaenger.net.NetException;
import net.x4a42.volksempfaenger.service.UpdateService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddSubscriptionActivity extends Activity implements
		OnClickListener {

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

	private static enum AddFeedTaskResult {
		SUCCEEDED, DOWNLOAD_FAILED, XML_EXCEPTION, IO_EXCEPTION, DUPLICATE, INSERT_ERROR
	}

	public class AddFeedTask extends AsyncTask<String, Void, AddFeedTaskResult> {

		private ProgressDialog dialog;
		private Uri newPodcastUri;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(AddSubscriptionActivity.this,
					getString(R.string.dialog_add_progress_title),
					getString(R.string.dialog_add_progress_message), true);
		}

		@Override
		protected AddFeedTaskResult doInBackground(String... params) {
			String feedUrl = params[0];
			FeedDownloader fd = new FeedDownloader(AddSubscriptionActivity.this);
			Feed feed;
			try {
				feed = fd.fetchFeed(feedUrl);
			} catch (NetException e) {
				Log.i(getClass().getSimpleName(), "Exception handled", e);
				return AddFeedTaskResult.DOWNLOAD_FAILED;
			} catch (FeedParserException e) {
				Log.i(getClass().getSimpleName(), "Exception handled", e);
				return AddFeedTaskResult.XML_EXCEPTION;
			}

			ContentValues values = new ContentValues();
			values.put(Podcast.TITLE, feed.getTitle());
			values.put(Podcast.DESCRIPTION, feed.getDescription());
			values.put(Podcast.FEED, feedUrl);
			values.put(Podcast.WEBSITE, feed.getWebsite());

			try {
				newPodcastUri = getContentResolver().insert(
						VolksempfaengerContentProvider.PODCAST_URI, values);
			} catch (Error.DuplicateException e) {
				return AddFeedTaskResult.DUPLICATE;
			} catch (Error.InsertException e) {
				return AddFeedTaskResult.INSERT_ERROR;
			}

			Intent updatePodcast = new Intent(AddSubscriptionActivity.this,
					UpdateService.class);
			updatePodcast.setData(newPodcastUri);
			updatePodcast.putExtra("first_sync", true);
			startService(updatePodcast);

			String feedImage = feed.getImage();
			if (feedImage != null) {
				// Try to download podcast logo
				LogoDownloader ld = new LogoDownloader(
						AddSubscriptionActivity.this);
				try {
					ld.fetchLogo(feedImage, ContentUris.parseId(newPodcastUri));
				} catch (Exception e) {
					// Who cares?
				}
			}
			return AddFeedTaskResult.SUCCEEDED;
		}

		@Override
		protected void onPostExecute(AddFeedTaskResult result) {
			dialog.dismiss();

			String message = null;

			switch (result) {
			case SUCCEEDED:
				Toast.makeText(AddSubscriptionActivity.this,
						R.string.message_podcast_successfully_added,
						Toast.LENGTH_SHORT).show();
				finish();
				return;
			case DOWNLOAD_FAILED:
				message = getString(R.string.message_podcast_feed_download_failed);
				break;
			case XML_EXCEPTION:
				message = getString(R.string.message_podcast_feed_parsing_failed);
				break;
			case IO_EXCEPTION:
				message = getString(R.string.message_podcast_feed_io_exception);
				break;
			case DUPLICATE:
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
