package net.x4a42.volksempfaenger.net;

import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;

public class ImageDownloader extends FileDownloader {

	public ImageDownloader(Context context) {
		super(context);
	}

	@Override
	protected HttpUriRequest getRequest(String url) {
		HttpUriRequest request = super.getRequest(url);
		request.addHeader("Accept", "image/*");
		return request;
	}
}