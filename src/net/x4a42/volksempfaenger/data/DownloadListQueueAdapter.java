package net.x4a42.volksempfaenger.data;

import java.io.File;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class DownloadListQueueAdapter extends SimpleCursorAdapter {
	static final String[] from = {
			DatabaseHelper.ExtendedEpisode.PODCAST_TITLE,
			DatabaseHelper.ExtendedEpisode.EPISODE_TITLE };
	static final int[] to = { R.id.podcast_title, R.id.episode_title };

	public DownloadListQueueAdapter(Context context, Cursor cursor) {
		super(context, R.layout.download_list_queue_row, cursor, from, to);
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		super.bindView(row, context, cursor);

		File podcastLogoFile = Utils.getPodcastLogoFile(context,
				cursor.getLong(cursor.getColumnIndex("podcast_id")));
		ImageView podcastLogo = (ImageView) row.findViewById(R.id.podcast_logo);
		if (podcastLogoFile.isFile()) {
			Bitmap podcastLogoBitmap = BitmapFactory.decodeFile(podcastLogoFile
					.getAbsolutePath());
			podcastLogo.setImageBitmap(podcastLogoBitmap);
		} else {
			podcastLogo.setImageResource(R.drawable.default_logo);
		}

	}
}
