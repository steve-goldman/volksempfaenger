package net.x4a42.volksempfaenger.data;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.x4a42.volksempfaenger.R;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class EpisodeListAdapter extends SimpleCursorAdapter {

	static final String[] from = { DatabaseHelper.Episode.TITLE,
			DatabaseHelper.Episode.DATE };
	static final int[] to = { R.id.episode_title, R.id.episode_date };

	static final Map<Integer, Integer> rowColorMap;
	static {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		map.put(DatabaseHelper.Enclosure.STATE_NEW, R.color.episode_title_new);
		map.put(DatabaseHelper.Enclosure.STATE_DOWNLOAD_QUEUED,
				R.color.episode_title_download_queued);
		map.put(DatabaseHelper.Enclosure.STATE_DOWNLOADING,
				R.color.episode_title_downloading);
		map.put(DatabaseHelper.Enclosure.STATE_DOWNLOADED,
				R.color.episode_title_downloaded);
		map.put(DatabaseHelper.Enclosure.STATE_LISTEN_QUEUED,
				R.color.episode_title_listen_queued);
		map.put(DatabaseHelper.Enclosure.STATE_LISTENING,
				R.color.episode_title_listening);
		map.put(DatabaseHelper.Enclosure.STATE_LISTENED,
				R.color.episode_title_listened);
		map.put(DatabaseHelper.Enclosure.STATE_DELETED,
				R.color.episode_title_deleted);
		rowColorMap = Collections.unmodifiableMap(map);
	}

	public EpisodeListAdapter(Context context, Cursor cursor) {
		super(context, R.layout.view_subscription_row, cursor, from, to);
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		super.bindView(row, context, cursor);

		// int episodeState = cursor.getInt(cursor
		// .getColumnIndex(DatabaseHelper.Enclosure.STATE));
		// TextView episodeTitle = (TextView)
		// row.findViewById(R.id.episode_title);
		// episodeTitle.setTextColor(rowColorMap.get(episodeState));

		Date date = new Date(cursor.getLong(cursor
				.getColumnIndex(DatabaseHelper.Episode.DATE)) * 1000);

		TextView episodeDate = (TextView) row.findViewById(R.id.episode_date);
		episodeDate.setText(DateFormat.getDateInstance().format(date));
	}
}
