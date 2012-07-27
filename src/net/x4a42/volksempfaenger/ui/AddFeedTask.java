package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.Error.DuplicateException;
import net.x4a42.volksempfaenger.data.Error.InsertException;
import net.x4a42.volksempfaenger.data.PodcastHelper;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.net.NetException;
import net.x4a42.volksempfaenger.receiver.BackgroundErrorReceiver;
import net.x4a42.volksempfaenger.ui.AddFeedTask.AddFeedTaskResult;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

public class AddFeedTask extends AsyncTask<String, Void, AddFeedTaskResult> {
	private final Context context;

	public AddFeedTask(Context context) {
		super();
		this.context = context;
	}

	static enum AddFeedTaskResult {
		SUCCEEDED, DOWNLOAD_FAILED, XML_EXCEPTION, IO_EXCEPTION, DUPLICATE, INSERT_ERROR
	}

	@Override
	protected AddFeedTaskResult doInBackground(String... params) {
		final String feedUrl = params[0];
		try {
			PodcastHelper.addFeed(context, feedUrl);
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
			Toast.makeText(context,
					R.string.message_podcast_successfully_added,
					Toast.LENGTH_SHORT).show();
			return;
		case DOWNLOAD_FAILED:
			message = context
					.getString(R.string.message_podcast_feed_download_failed);
			break;
		case XML_EXCEPTION:
			message = context
					.getString(R.string.message_podcast_feed_parsing_failed);
			break;
		case IO_EXCEPTION:
			message = context
					.getString(R.string.message_podcast_feed_io_exception);
			break;
		case DUPLICATE:
			message = context.getString(R.string.message_podcast_already_added);
			break;
		default:
			break;
		}

		if (message != null) {
			Intent intent = BackgroundErrorReceiver.getBackgroundErrorIntent(
					context.getString(R.string.dialog_error_title), message,
					BackgroundErrorReceiver.ERROR_ADD);
			context.sendOrderedBroadcast(intent, null);
		}
	}

}
