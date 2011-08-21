package net.x4a42.volksempfaenger.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedParser;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;

public class FeedDownloader {
	private Context context;

	public FeedDownloader(Context context) {
		this.context = context;
	}

	private String getUserAgent() {
		String name = context.getString(R.string.app_name_ascii);
		String version = VolksempfaengerApplication.getPackageInfo(context).versionName;
		return String.format("%s/%s", name, version);
	}

	private AndroidHttpClient getHttpClient() {
		return AndroidHttpClient.newInstance(getUserAgent(), context);
	}

	public Feed fetchFeed(String url) throws NetException, FeedParserException {
		AndroidHttpClient client = getHttpClient();
		HttpGet request;
		try {
			request = new HttpGet(url);
		} catch (IllegalArgumentException e) {
			Log.w(getClass().getSimpleName(), e);
			throw new NetException(e);
		}
		request.addHeader("Accept", "application/atom+xml, application/rss+xml");
		try {
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
			Feed feed =  FeedParser.parse(rd);
			client.close();
			return feed;
		} catch (IllegalStateException e) {
			Log.w(getClass().getSimpleName(), e);
			throw new NetException(e);
		} catch (IOException e) {
			Log.w(getClass().getSimpleName(), e);
			throw new NetException(e);
		}
	}
}