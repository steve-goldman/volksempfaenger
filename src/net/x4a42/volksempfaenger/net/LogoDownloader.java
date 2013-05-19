package net.x4a42.volksempfaenger.net;

import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.misc.StorageException;
import android.content.Context;

public class LogoDownloader extends ImageDownloader {

	public LogoDownloader(Context context) {
		super(context);
	}

	public void fetchLogo(String url, long podcastId) throws NetException,
			StorageException {
		fetchFile(url, null, Utils.getPodcastLogoFile(getContext(), podcastId));
	}
}
