package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.data.Error;
import net.x4a42.volksempfaenger.data.Error.DuplicateException;
import net.x4a42.volksempfaenger.data.Error.InsertException;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
		setTitle(R.string.title_add_subscription);
		setContentView(R.layout.add_subscription);

		LayoutParams params = getWindow().getAttributes();
		params.width = LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(params);

		editTextUrl = (EditText) findViewById(R.id.edittext_url);
		buttonAdd = (Button) findViewById(R.id.button_add);
		buttonCancel = (Button) findViewById(R.id.button_cancel);

		buttonAdd.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
	}

	@Override
	public void onStart() {
		super.onStart();

		Uri data = getIntent().getData();
		if (data != null) {
			editTextUrl.setText(data.toString());
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		ExternalStorageHelper.assertExternalStorageWritable(this);
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

	public static boolean addFeed(Context context, String url)
			throws NetException, FeedParserException, Error.DuplicateException,
			Error.InsertException {
		final FeedDownloader fd = new FeedDownloader(context);
		final Feed feed = fd.fetchFeed(url);
		final ContentValues values = new ContentValues();
		values.put(Podcast.TITLE, feed.title);
		values.put(Podcast.DESCRIPTION, feed.description);
		values.put(Podcast.FEED, url);
		values.put(Podcast.WEBSITE, feed.website);

		final Uri newPodcastUri = context.getContentResolver().insert(
				VolksempfaengerContentProvider.PODCAST_URI, values);

		final Intent updatePodcast = new Intent(context, UpdateService.class);
		updatePodcast.setData(newPodcastUri);
		updatePodcast.putExtra("first_sync", true);
		context.startService(updatePodcast);

		final String feedImage = feed.image;
		if (feedImage != null) {
			// Try to download podcast logo
			final LogoDownloader ld = new LogoDownloader(context);
			try {
				ld.fetchLogo(feedImage, ContentUris.parseId(newPodcastUri));
			} catch (Exception e) {
				// Who cares?
			}
		}
		return false;
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

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(AddSubscriptionActivity.this,
					getString(R.string.dialog_add_progress_title),
					getString(R.string.dialog_add_progress_message), true);
		}

		@Override
		protected AddFeedTaskResult doInBackground(String... params) {
			final String feedUrl = params[0];
			try {
				AddSubscriptionActivity.addFeed(AddSubscriptionActivity.this,
						feedUrl);
			} catch (NetException e) {
				Log.i(this, "Exception handled", e);
				return AddFeedTaskResult.DOWNLOAD_FAILED;
			} catch (FeedParserException e) {
				Log.i(this, "Exception handled", e);
				return AddFeedTaskResult.XML_EXCEPTION;
			} catch (DuplicateException e) {
				return AddFeedTaskResult.DUPLICATE;
			} catch (InsertException e) {
				return AddFeedTaskResult.INSERT_ERROR;
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
