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
import android.widget.TextView;

public class DownloadListFinishedAdapter extends DownloadListAdapter {

	private static final String[] from = { DownloadManager.COLUMN_TITLE };
	private static final int[] to = { R.id.download_title };

	public DownloadListFinishedAdapter(Context context, Cursor cursor) {

		super(context, R.layout.download_list_finished_row, cursor, from, to);

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		super.bindView(view, context, cursor);

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
