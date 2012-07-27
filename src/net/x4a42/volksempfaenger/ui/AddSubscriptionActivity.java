package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.Error.DuplicateException;
import net.x4a42.volksempfaenger.data.Error.InsertException;
import net.x4a42.volksempfaenger.data.PodcastHelper;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.net.NetException;
import net.x4a42.volksempfaenger.receiver.BackgroundErrorReceiver;
import android.app.Activity;
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

	private void subscribeToFeed() {
		String feedUrl = editTextUrl.getText().toString();

		new AddFeedTask().execute(feedUrl);
		finish();
	}

	private static enum AddFeedTaskResult {
		SUCCEEDED, DOWNLOAD_FAILED, XML_EXCEPTION, IO_EXCEPTION, DUPLICATE, INSERT_ERROR
	}

	public class AddFeedTask extends AsyncTask<String, Void, AddFeedTaskResult> {

		@Override
		protected AddFeedTaskResult doInBackground(String... params) {
			final String feedUrl = params[0];
			try {
				PodcastHelper.addFeed(AddSubscriptionActivity.this, feedUrl);
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
			String message = null;

			switch (result) {
			case SUCCEEDED:
				Toast.makeText(AddSubscriptionActivity.this,
						R.string.message_podcast_successfully_added,
						Toast.LENGTH_SHORT).show();
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
			default:
				break;
			}

			if (message != null) {
				Intent intent = BackgroundErrorReceiver
						.getBackgroundErrorIntent(
								getString(R.string.dialog_error_title),
								message, BackgroundErrorReceiver.ERROR_ADD);
				sendOrderedBroadcast(intent, null);
			}
		}

	}
}
