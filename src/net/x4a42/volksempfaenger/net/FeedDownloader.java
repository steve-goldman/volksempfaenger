package net.x4a42.volksempfaenger.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedParser;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import android.content.Context;
import net.x4a42.volksempfaenger.Log;

public class FeedDownloader extends Downloader {

	public FeedDownloader(Context context) {
		super(context);
	}

	public Feed fetchFeed(String url) throws NetException, FeedParserException {
		try {
			HttpURLConnection connection = getConnection(url);

			connection.setRequestProperty("Accept",
					"application/atom+xml, application/rss+xml");
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			Feed feed = FeedParser.parse(rd);
			connection.disconnect();
			return feed;
		} catch (FeedParserException e) {
			throw e;
		} catch (Exception e) {
			Log.i(this, "Handled Exception:", e);
			throw new NetException(e);
		}
	}
}
