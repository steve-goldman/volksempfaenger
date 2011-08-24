package net.x4a42.volksempfaenger.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedParser;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;

public class FeedDownloader extends Downloader {

	public FeedDownloader(Context context) {
		super(context);
	}

	private AndroidHttpClient getHttpClient() {
		return AndroidHttpClient.newInstance(getUserAgent(), getContext());
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
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			Feed feed = FeedParser.parse(rd);
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