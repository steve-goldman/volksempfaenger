package net.x4a42.volksempfaenger.net;

import android.content.Context;
import android.net.http.HttpResponseCache;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader {
	private static final int CONNECTION_TIMEOUT = 30000;

	private Context context;
	private static HttpResponseCache cache;
	private String versionName;

	public Downloader(Context context) {
		this.context = context;
		try
		{
			versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		}
		catch (Exception e)
		{
			versionName = "this-version";
		}
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
		return String.format("%s/%s", name, versionName);
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
