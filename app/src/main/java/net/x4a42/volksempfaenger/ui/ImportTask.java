package net.x4a42.volksempfaenger.ui;

import java.util.LinkedList;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.PodcastHelper;
import net.x4a42.volksempfaenger.feedparser.SubscriptionTree;
import net.x4a42.volksempfaenger.receiver.BackgroundErrorReceiver;
import net.x4a42.volksempfaenger.service.UpdateService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

class ImportTask extends AsyncTask<SubscriptionTree[], Void, Void> {
	private final Context context;

	ImportTask(Context context) {
		this.context = context;
	}

	final LinkedList<String> failed = new LinkedList<String>();

	@Override
	protected void onPreExecute() {
		UpdateService.Status.startGlobalUpdate();
	}

	@Override
	protected Void doInBackground(SubscriptionTree[]... params) {
		SubscriptionTree[] checkedItems = params[0];
		for (SubscriptionTree subscription : checkedItems) {
			// TODO finer grained exception handling
			try {
				PodcastHelper.addFeed(context, subscription.url);
			} catch (Exception e) {
				if (subscription == null) {
					continue;
				}
				String name;
				if (subscription.title == null) {
					name = subscription.url;
				} else {
					name = subscription.title;
				}
				failed.add(name);

			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void arg0) {
		UpdateService.Status.stopGlobalUpdate();
		if (failed.size() > 0) {
			Intent intent = BackgroundErrorReceiver.getBackgroundErrorIntent(
					context.getString(R.string.title_import_error),
					context.getString(R.string.message_error_import),
					BackgroundErrorReceiver.ERROR_IMPORT);
			StringBuilder strBuilder = new StringBuilder();
			for (String podcast : failed) {
				strBuilder.append(podcast);
				strBuilder.append("\n");
			}
			String text = strBuilder.substring(0, strBuilder.length() - 1);
			intent.putExtra(ImportFileActivity.EXTRA_IMPORT_FAILED_ITEMS, text);
			context.sendOrderedBroadcast(intent, null);
		}
	}
}