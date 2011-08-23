package net.x4a42.volksempfaenger.data;

import java.text.DateFormat;
import java.util.Date;

import net.x4a42.volksempfaenger.R;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class EpisodeListAdapter extends SimpleCursorAdapter {
	static final String[] FROM = { DbHelper.Episode.TITLE,
			DbHelper.Episode.DATE };
	static final int[] TO = { R.id.episode_title, R.id.episode_date };

	public EpisodeListAdapter(Context context, Cursor cursor) {
		super(context, R.layout.view_subscription_row, cursor, FROM, TO);
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		super.bindView(row, context, cursor);

		Date date = new Date(cursor.getLong(cursor
				.getColumnIndex(DbHelper.Episode.DATE)) * 1000);

		TextView episodeDate = (TextView) row.findViewById(R.id.episode_date);
		episodeDate.setText(DateFormat.getDateInstance().format(date));
	}

}