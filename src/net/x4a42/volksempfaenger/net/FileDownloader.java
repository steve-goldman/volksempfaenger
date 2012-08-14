package net.x4a42.volksempfaenger.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.misc.StorageException;
import android.content.Context;

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

			FileOutputStream out = new FileOutputStream(target);
			InputStream in = connection.getInputStream();

			Utils.copyStream(in, out);
			in.close();
			out.close();
			connection.disconnect();

		} catch (Exception e) {

			Log.i(this, "Handled Exception:", e);
			if (deleteTargetOnFailure) {
				target.delete();
			}
			throw new NetException(e);

		}
	}
}
