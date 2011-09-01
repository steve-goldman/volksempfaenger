package net.x4a42.volksempfaenger.data;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.x4a42.volksempfaenger.R;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class EpisodeListAdapter extends SimpleCursorAdapter {

	static final String[] from = { DatabaseHelper.ExtendedEpisode.EPISODE_TITLE };
	static final int[] to = { R.id.episode_title };

	static Map<Integer, Integer> rowColorMap;

	private static synchronized void initRowColorMap(Context context) {
		if (rowColorMap != null) {
			return;
		}
		Resources res = context.getResources();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		map.put(DatabaseHelper.Episode.STATE_NEW,
				res.getColor(R.color.episode_title_new));
		map.put(DatabaseHelper.Episode.STATE_DOWNLOADING,
				res.getColor(R.color.episode_title_downloading));
		map.put(DatabaseHelper.Episode.STATE_READY,
				res.getColor(R.color.episode_title_ready));
		map.put(DatabaseHelper.Episode.STATE_LISTENING,
				res.getColor(R.color.episode_title_listening));
		map.put(DatabaseHelper.Episode.STATE_LISTENED,
				res.getColor(R.color.episode_title_listened));
		rowColorMap = Collections.unmodifiableMap(map);
	}

	public EpisodeListAdapter(Context context, Cursor cursor) {
		super(context, R.layout.view_subscription_row, cursor, from, to);
		if (rowColorMap == null) {
			initRowColorMap(context);
		}
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		super.bindView(row, context, cursor);

		int episodeState = cursor.getInt(cursor
				.getColumnIndex(DatabaseHelper.ExtendedEpisode.EPISODE_STATE));
		TextView episodeTitle = (TextView) row.findViewById(R.id.episode_title);
		episodeTitle.setTextColor(rowColorMap.get(episodeState));

		Date date = new Date(
				cursor.getLong(cursor
						.getColumnIndex(DatabaseHelper.ExtendedEpisode.EPISODE_DATE)) * 1000);

		TextView episodeDate = (TextView) row.findViewById(R.id.episode_date);
		episodeDate.setText(DateFormat.getDateInstance().format(date));
	}
}
