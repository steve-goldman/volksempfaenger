package net.x4a42.volksempfaenger.net;

import java.io.File;

import net.x4a42.volksempfaenger.Utils;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;

public class EnclosureDownloader extends Downloader {
	private DownloadManager dm;
	private int allowedNetworks;

	public EnclosureDownloader(Context context, boolean allowWifi,
			boolean allowMobile) {
		super(context);

		allowedNetworks = 0;
		if (allowWifi) {
			allowedNetworks |= DownloadManager.Request.NETWORK_WIFI;
		}
		if (allowMobile) {
			allowedNetworks |= DownloadManager.Request.NETWORK_MOBILE;
		}

		dm = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
	}

	public long downloadEnclosure(long id, String url, CharSequence title) {
		File target = Utils.getEnclosureFile(getContext(), id);
		target.getParentFile().mkdirs();
		Request request = new Request(Uri.parse(url));
		request.addRequestHeader("User-Agend", getUserAgent());
		request.setAllowedNetworkTypes(allowedNetworks);
		request.setTitle(title);
		request.setDestinationUri(Uri.fromFile(target));
		return dm.enqueue(request);
	}

	public int getRunningDownloadCount() {
		Query query = new Query();
		query.setFilterByStatus(DownloadManager.STATUS_PENDING
				| DownloadManager.STATUS_RUNNING
				| DownloadManager.STATUS_PAUSED);
		return dm.query(query).getCount();
	}
}
