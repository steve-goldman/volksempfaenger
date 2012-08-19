package net.x4a42.volksempfaenger.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.misc.StorageException;
import android.content.Context;

public class FileDownloader extends Downloader {

	public enum Result {
		OK, CACHED
	}

	public FileDownloader(Context context) {
		super(context);
	}

	public Result fetchFile(String url, CacheInformation cacheInfo, File target)
			throws NetException, StorageException {

		if (cacheInfo != null && !cacheInfo.expired()) {
			// file is not expired - no need to re-fetch
			return Result.CACHED;
		}

		boolean deleteTargetOnFailure = false;

		try {

			HttpURLConnection connection = getConnection(url);

			if (cacheInfo != null) {
				cacheInfo.prepareConnection(connection);
			}

			target.getParentFile().mkdirs();
			target.createNewFile();
			deleteTargetOnFailure = true;

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
				return Result.CACHED;
			} else if (cacheInfo != null) {
				cacheInfo.updateFromConnection(connection);
			}

			OutputStream out = new FileOutputStream(target);
			InputStream in = connection.getInputStream();

			byte[] buf = new byte[1024];
			int i;
			while ((i = in.read(buf)) > 0) {
				out.write(buf, 0, i);
			}

			out.close();
			in.close();
			connection.disconnect();

			return Result.OK;

		} catch (Exception e) {

			Log.i(this, "Handled Exception:", e);
			if (deleteTargetOnFailure) {
				target.delete();
			}
			throw new NetException(e);

		}
	}
}
