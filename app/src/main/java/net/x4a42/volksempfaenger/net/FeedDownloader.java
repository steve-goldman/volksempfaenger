package net.x4a42.volksempfaenger.net;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

import net.x4a42.volksempfaenger.misc.StorageException;
import android.content.Context;

public class FeedDownloader extends FileDownloader {

	private static final String HEADER_ACCEPT = "application/atom+xml, application/rss+xml, application/xml, text/xml";

	public FeedDownloader(Context context) {
		super(context);
	}

	@Override
	public HttpURLConnection getConnection(String url) throws IOException {
		HttpURLConnection connection = super.getConnection(url);
		connection.setRequestProperty("Accept", HEADER_ACCEPT);
		connection.setUseCaches(false);
		return connection;
	}

	public Result fetchFeed(String url, CacheInformation cacheInfo, File target)
			throws NetException, StorageException {
		if (cacheInfo != null) {
			cacheInfo.expires = 0;
		}
		return fetchFile(url, cacheInfo, target);

	}

}
