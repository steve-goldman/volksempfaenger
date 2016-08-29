package net.x4a42.volksempfaenger.net;

import java.net.HttpURLConnection;

public class CacheInformation {
	public long lastModified;
	public long expires;
	public String eTag;

	public boolean expired() {
		return (expires == 0) ? true : expires > System.currentTimeMillis();
	}

	public void prepareConnection(HttpURLConnection connection) {
		connection.setIfModifiedSince(lastModified);
		if (eTag != null) {
			connection.setRequestProperty("If-None-Match", eTag);
		}
	}

	public void updateFromConnection(HttpURLConnection connection) {
		lastModified = connection.getLastModified();
		expires = connection.getExpiration();
		eTag = connection.getHeaderField("ETag");
	}
}
