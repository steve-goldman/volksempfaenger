package net.x4a42.volksempfaenger.data;

import java.io.File;
import java.util.Map;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadListRunningAdapter extends DownloadListAdapter {

	private static final String[] from = { DownloadManager.COLUMN_TITLE };
	private static final int[] to = { R.id.download_title };

	public DownloadListRunningAdapter(Context context, Cursor cursor) {
		super(context, R.layout.download_list_running_row, cursor, from, to);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		super.bindView(view, context, cursor);

		TextView downloadProgressText = (TextView) view
				.findViewById(R.id.download_progress_text);
		ProgressBar downloasProgressBar = (ProgressBar) view
				.findViewById(R.id.download_progress_bar);
		try {
			int percent = (int) (100 * cursor
					.getLong(cursor
							.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)) / cursor
					.getLong(cursor
							.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)));
			if (percent > 100) {
				throw (Exception) null;
			} else if (percent < 0) {
				throw (Exception) null;
			}
			downloasProgressBar.setProgress(percent);
			downloadProgressText.setText(percent + "%");
		} catch (Exception e) {
			// TODO
			downloadProgressText.setText("??%");
		}

		Map<String, String> info = dataMap.get(cursor.getLong(cursor
				.getColumnIndex(DownloadManager.COLUMN_ID)));
		if (info == null) {
			// Actually this shouldn't happen but to prevent crashes we just
			// finish
			return;
		}

		File podcastLogoFile = Utils.getPodcastLogoFile(context,
				Long.valueOf(info.get("podcast_id")));
		ImageView podcastLogo = (ImageView) view
				.findViewById(R.id.podcast_logo);
		if (podcastLogoFile.isFile()) {
			Bitmap podcastLogoBitmap = BitmapFactory.decodeFile(podcastLogoFile
					.getAbsolutePath());
			podcastLogo.setImageBitmap(podcastLogoBitmap);
		} else {
			podcastLogo.setImageResource(R.drawable.default_logo);
		}

		TextView podcastTitle = (TextView) view
				.findViewById(R.id.podcast_title);
		podcastTitle.setText(info.get("podcast_title"));

	}

}
