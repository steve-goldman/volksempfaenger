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

public class LogoDownloader extends Downloader {

	public LogoDownloader(Context context) {
		super(context);
	}

	private AndroidHttpClient getHttpClient() {
		return AndroidHttpClient.newInstance(getUserAgent(), getContext());
	}

	public void fetchLogo(String url, String target) throws NetException {
		AndroidHttpClient client = getHttpClient();
		HttpGet request;
		try {
			request = new HttpGet(url);
		} catch (IllegalArgumentException e) {
			Log.w(getClass().getSimpleName(), e);
			throw new NetException(e);
		}
		request.addHeader("Accept", "image/*");
		//HttpResponse response = client.execute(request);
		//response.getEntity().getContent();
	}
}