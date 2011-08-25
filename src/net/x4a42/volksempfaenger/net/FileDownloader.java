package net.x4a42.volksempfaenger.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.x4a42.volksempfaenger.data.StorageException;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;

public class FileDownloader extends Downloader {

	public FileDownloader(Context context) {
		super(context);
	}

	public void fetchFile(String url, File target) throws NetException,
			StorageException {

		boolean deleteTargetOnFailure = false;

		try {

			HttpURLConnection connection = getConnection(url);

			target.getParentFile().mkdirs();
			target.createNewFile();
			deleteTargetOnFailure = true;

			BufferedOutputStream bufout = new BufferedOutputStream(new FileOutputStream(target));
			BufferedInputStream bufin = new BufferedInputStream(connection.getInputStream());

			byte[] buf = new byte[1024];
			int i;
			while ((i = bufin.read(buf)) > 0) {
				bufout.write(buf, 0, i);
			}

			bufout.flush();
			bufout.close();
			bufin.close();
			connection.disconnect();

		} catch (Exception e) {

			Log.i(getClass().getSimpleName(), "Handled Exception:", e);
			if (deleteTargetOnFailure) {
				target.delete();
			}
			throw new NetException(e);

		}
	}
}