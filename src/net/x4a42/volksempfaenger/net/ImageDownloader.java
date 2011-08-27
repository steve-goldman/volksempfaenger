package net.x4a42.volksempfaenger.net;

import java.io.IOException;
import java.net.HttpURLConnection;

import android.content.Context;

public class ImageDownloader extends FileDownloader {

	public ImageDownloader(Context context) {
		super(context);
	}

	@Override
	public HttpURLConnection getConnection(String url) throws IOException {
		HttpURLConnection connection = super.getConnection(url);
		connection.setRequestProperty("Accept", "image/*");
		return connection;
	}

}
