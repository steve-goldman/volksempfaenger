package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.Error.DuplicateException;
import net.x4a42.volksempfaenger.data.Error.InsertException;
import net.x4a42.volksempfaenger.data.PodcastHelper;
import net.x4a42.volksempfaenger.receiver.BackgroundErrorReceiver;
import net.x4a42.volksempfaenger.service.UpdateService;
import net.x4a42.volksempfaenger.ui.AddFeedTask.AddFeedTaskResult;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

public class AddFeedTask extends AsyncTask<String, Void, AddFeedTaskResult> {

	private final Context context;
	private Toast toast;

	public AddFeedTask(Context context) {
		super();
		this.context = context;
	}

	static enum AddFeedTaskResult {
		SUCCEEDED, DUPLICATE, INSERT_ERROR
	}

	@Override
	protected void onPreExecute() {
		UpdateService.Status.startGlobalUpdate();
		toast = Toast.makeText(context, R.string.message_subscribing_podcast,
				Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	protected AddFeedTaskResult doInBackground(String... params) {
		final String feedUrl = params[0];
		try {
			PodcastHelper.addFeed(context, feedUrl);
		} catch (DuplicateException e) {
			return AddFeedTaskResult.DUPLICATE;
		} catch (InsertException e) {
			return AddFeedTaskResult.INSERT_ERROR;
		}
		return AddFeedTaskResult.SUCCEEDED;
	}

	@Override
	protected void onPostExecute(AddFeedTaskResult result) {
		UpdateService.Status.stopGlobalUpdate();
		String message = null;

		switch (result) {
		case DUPLICATE:
			message = context.getString(R.string.message_podcast_already_added);
			break;
		case INSERT_ERROR:
			// TODO report with ACRA
			message = context.getString(R.string.unknown_error_occured);
			break;
		default:
			return;
		}

		toast.cancel();
		Intent intent = BackgroundErrorReceiver.getBackgroundErrorIntent(
				context.getString(R.string.dialog_error_title), message,
				BackgroundErrorReceiver.ERROR_ADD);
		context.sendOrderedBroadcast(intent, null);
	}

}
