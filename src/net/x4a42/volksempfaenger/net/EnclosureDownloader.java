package net.x4a42.volksempfaenger.net;

import net.x4a42.volksempfaenger.R;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class EnclosureDownloader {
	private Context context;
	private DownloadManager dm;

	public EnclosureDownloader(Context context) {
		this.context = context;
		dm = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
	}

	public void downloadEnclosure(String url) {
		Request request = new Request(Uri.parse(url));
		//request.setDestinationUri(uri);
		dm.enqueue(request);

		// registerReceiver(receiver, new IntentFilter(
		// DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}
}
