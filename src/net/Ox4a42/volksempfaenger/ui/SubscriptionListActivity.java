package net.Ox4a42.volksempfaenger.ui;

import net.Ox4a42.volksempfaenger.R;
import net.Ox4a42.volksempfaenger.data.DbHelper;
import net.Ox4a42.volksempfaenger.data.SubscriptionListAdapter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

public class SubscriptionListActivity extends BaseActivity {
	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	ListView listTimeline;
	SubscriptionListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscription_list);

		db = getApp().getReadableDatabase();
		listTimeline = (ListView) findViewById(R.id.subscription_list);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}

	@Override
	protected void onResume() {
		super.onResume();

		cursor = db.query(DbHelper.Podcast._TABLE, null, null, null, null,
				null, DbHelper.Podcast.TITLE);
		startManagingCursor(cursor);

		adapter = new SubscriptionListAdapter(this, cursor);
		listTimeline.setAdapter(adapter);
	}

}