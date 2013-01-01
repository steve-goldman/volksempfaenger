package net.x4a42.volksempfaenger.net;

import java.io.File;

import net.x4a42.volksempfaenger.PreferenceKeys;
import net.x4a42.volksempfaenger.Utils;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

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
		String filename = Utils.filenameFromUrl(url);
		File target = Utils.getEnclosureFile(getContext(), id, filename);
		target.getParentFile().mkdirs();
		Request request = new Request(Uri.parse(url));
		request.addRequestHeader("User-Agent", getUserAgent());
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

	public int getMaxConcurrentDownloads() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		int m;
		try {
			m = Integer.valueOf(prefs.getString(
					PreferenceKeys.DOWNLOAD_CONCURRENT, null));
		} catch (NumberFormatException e) {
			return 1;
		}
		if (m < 1) {
			return 1;
		}
		return m;
	}

	public int getFreeDownloadSlots() {
		int a = getMaxConcurrentDownloads() - getRunningDownloadCount();
		if (a < 0) {
			return 0;
		}
		return a;
	}

	public DownloadManager getDownloadManager() {
		return dm;
	}

}
