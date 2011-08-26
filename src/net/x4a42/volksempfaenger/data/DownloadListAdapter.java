package net.x4a42.volksempfaenger.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class DownloadListAdapter extends SimpleCursorAdapter {

	private static final String[] from = { DownloadManager.COLUMN_TITLE };
	private static final int[] to = { R.id.download_title };

	private DatabaseHelper dbHelper;
	private Map<Long, Map<String, String>> dataMap;

	public DownloadListAdapter(Context context, Cursor cursor) {

		super(context, R.layout.download_list_row, cursor, from, to);
		dbHelper = DatabaseHelper.getInstance(context);
		onContentChanged();

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		super.bindView(view, context, cursor);

		TextView downloadProgress = (TextView) view
				.findViewById(R.id.download_progress);
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
			downloadProgress.setText(percent + "%");
		} catch (Exception e) {
			// TODO
			downloadProgress.setText("??%");
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

	@Override
	protected void onContentChanged() {
		super.onContentChanged();

		Log.d(getClass().getSimpleName(), "onContentChanged()");

		Cursor c = dbHelper
				.getReadableDatabase()
				.rawQuery(
						"SELECT enclosure.download_id AS _id, podcast._id AS podcast_id, "
								+ "podcast.title AS podcast_title, episode._id AS episode_id, "
								+ "episode.title AS episode_title, enclosure._id AS enclosure_id "
								+ "FROM enclosure JOIN episode ON episode._id = enclosure.episode_id "
								+ "JOIN podcast ON podcast._id = episode.podcast_id "
								+ "WHERE enclosure.download_id IS NOT NULL",
						null);

		String[] cols = c.getColumnNames();
		dataMap = new HashMap<Long, Map<String, String>>(c.getCount());

		while (c.moveToNext()) {
			Map<String, String> m = new HashMap<String, String>(
					c.getColumnCount());
			for (int i = 0; i < c.getColumnCount(); i++) {
				m.put(cols[i], c.getString(i));
			}
			dataMap.put(c.getLong(c.getColumnIndex("_id")), m);
		}

		c.close();
	}

}