package net.x4a42.volksempfaenger.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.StorageException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;

public class FileDownloader extends Downloader {

	public FileDownloader(Context context) {
		super(context);
	}

	protected AndroidHttpClient getHttpClient() {
		return AndroidHttpClient.newInstance(getUserAgent(), getContext());
	}
	
	protected HttpUriRequest getRequest(String url) {
		return new HttpGet(url);
	}

	public void fetchFile(String url, File target) throws NetException,
			StorageException {

		AndroidHttpClient client = getHttpClient();
		HttpUriRequest request;

		try {
			request = getRequest(url);
		} catch (IllegalArgumentException e) {
			Log.w(getClass().getSimpleName(), e);
			throw new NetException(e);
		}

		target.getParentFile().mkdirs();
		try {
			target.createNewFile();
		} catch (IOException e) {
			Log.w(getClass().getName(), e);
			throw new StorageException(e);
		}

		OutputStream out;
		try {
			out = new FileOutputStream(target);
		} catch (FileNotFoundException e) {
			Log.w(getClass().getName(), e);
			throw new StorageException(e);
		}

		InputStream in;
		try {
			HttpResponse response = client.execute(request);
			in = response.getEntity().getContent();
		} catch (IllegalStateException e) {
			Log.w(getClass().getName(), e);
			target.delete();
			throw new NetException(e);
		} catch (IOException e) {
			Log.w(getClass().getName(), e);
			target.delete();
			throw new NetException(e);
		}

		BufferedInputStream bufin = new BufferedInputStream(in);
		BufferedOutputStream bufout = new BufferedOutputStream(out);

		byte[] buf = new byte[1024];
		int i;
		try {
			while ((i = bufin.read(buf)) > 0) {
				bufout.write(buf, 0, i);
			}
			bufout.flush();
			bufout.close();
			bufin.close();
			client.close();
		} catch (IOException e) {
			Log.w(getClass().getName(), e);
			target.delete();
			throw new StorageException(e);
		}
	}
}