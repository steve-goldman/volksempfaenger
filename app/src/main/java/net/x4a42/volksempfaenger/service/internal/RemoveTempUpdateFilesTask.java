package net.x4a42.volksempfaenger.service.internal;

import java.io.File;
import java.io.FilenameFilter;

import android.content.Context;
import android.os.AsyncTask;

public class RemoveTempUpdateFilesTask extends AsyncTask<Void, Void, Void> {

	private static final String PREFIX = FeedDownloaderRunnable.TEMP_FILE_PREFIX;
	private static final String SUFFIX = FeedDownloaderRunnable.TEMP_FILE_SUFFIX;

	private Context context;

	public RemoveTempUpdateFilesTask(Context context) {
		this.context = context;
	}

	@Override
	protected Void doInBackground(Void... params) {

		File[] tempFiles = context.getCacheDir().listFiles(
				new FilenameFilter() {

					@Override
					public boolean accept(File dir, String filename) {
						return filename.startsWith(PREFIX)
								&& filename.endsWith(SUFFIX);
					}
				});

		for (File file : tempFiles) {
			file.delete();
		}

		return null;

	}
}