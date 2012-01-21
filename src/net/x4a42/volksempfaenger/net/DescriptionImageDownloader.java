package net.x4a42.volksempfaenger.net;

import java.io.File;

import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.StorageException;
import android.content.Context;

public class DescriptionImageDownloader extends ImageDownloader {

	public DescriptionImageDownloader(Context context) {
		super(context);
	}

	public String fetchImage(String url) throws NetException, StorageException {
		File target = Utils.getDescriptionImageFile(getContext(), url);
		if (!target.isFile()) {
			fetchFile(url, target);
		}
		return target.getAbsolutePath();
	}

}
