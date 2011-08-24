package net.x4a42.volksempfaenger.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import android.content.Context;

public abstract class Downloader {

	private Context context;

	public Downloader(Context context) {
		this.context = context;
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
		return connection;
	}

}
