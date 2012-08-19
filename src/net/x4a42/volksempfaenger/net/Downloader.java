package net.x4a42.volksempfaenger.net;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import android.content.Context;
import android.net.http.HttpResponseCache;

public class Downloader {
	private static final int CONNECTION_TIMEOUT = 30000;

	private Context context;
	private static HttpResponseCache cache;

	public Downloader(Context context) {
		this.context = context;
		initCache();
	}

	private void initCache() {
		if (cache != null)
			return;
		synchronized (Downloader.class) {
			if (cache != null)
				return;
			cache = HttpResponseCache.getInstalled();
			if (cache != null)
				return;
			try {
				File path = Utils.joinPath(context.getExternalCacheDir(),
						"http");
				HttpResponseCache.install(path, 16 * 1024 * 1024 /* 16 MiB */);
			} catch (Exception e) {
				Log.i(this, "Could not install HttpResponseCache", e);
			}
		}
	}

	protected Context getContext() {
		return context;
	}

	public String getUserAgent() {
		String name = context.getString(R.string.app_name_ascii);
		String version = VolksempfaengerApplication.getPackageInfo(context).versionName;
		return String.format("%s/%s", name, version);
	}

	public HttpURLConnection getConnection(String url) throws IOException {
		URL urlObj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) urlObj
				.openConnection();
		connection.setRequestProperty("User-Agent", getUserAgent());
		connection.setInstanceFollowRedirects(true);
		connection.setUseCaches(true);
		connection.setConnectTimeout(CONNECTION_TIMEOUT);
		return connection;
	}

}
