package net.x4a42.volksempfaenger.data;

import net.x4a42.volksempfaenger.R;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;

public class SubscriptionListAdapter extends SimpleCursorAdapter {
	static final String[] FROM = { DbHelper.Podcast.TITLE, DbHelper.Podcast.DESCRIPTION };
	static final int[] TO = { R.id.podcast_title, R.id.podcast_description };

	public SubscriptionListAdapter(Context context, Cursor cursor) {
		super(context, R.layout.subscription_list_row, cursor, FROM, TO);
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		super.bindView(row, context, cursor);

		//String title = cursor.getString(cursor.getColumnIndex(DbHelper.Podcast.TITLE));
		//TextView podcastTitle = (TextView) row.findViewById(R.id.podcast_title);
		//podcastTitle.setText(title);
	}

}