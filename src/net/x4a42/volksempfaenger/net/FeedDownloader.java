package net.x4a42.volksempfaenger.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedParser;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import android.content.Context;

public class FeedDownloader extends Downloader {

	public FeedDownloader(Context context) {
		super(context);
	}

	public Feed fetchFeed(String url, CacheInformation cacheInfo)
			throws NetException, FeedParserException {
		if (cacheInfo != null && !cacheInfo.expired()) {
			// feed is not expired - no need to re-fetch
			return null;
		}
		try {
			HttpURLConnection connection = getConnection(url);
			if (cacheInfo != null) {
				cacheInfo.prepareConnection(connection);
			}
			connection
					.setRequestProperty("Accept",
							"application/atom+xml, application/rss+xml, application/xml, text/xml");
			if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
				return null;
			} else if (cacheInfo != null) {
				cacheInfo.updateFromConnection(connection);
			}
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
