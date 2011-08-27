package net.x4a42.volksempfaenger.data;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

public class DownloadListAdapter extends SimpleCursorAdapter {

	protected DatabaseHelper dbHelper;
	protected Map<Long, Map<String, String>> dataMap;

	public DownloadListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		dbHelper = DatabaseHelper.getInstance(context);
		onContentChanged();
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
