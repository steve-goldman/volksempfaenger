package net.Ox4a42.volksempfaenger.ui;

import net.Ox4a42.volksempfaenger.R;
import net.Ox4a42.volksempfaenger.data.DbHelper;
import net.Ox4a42.volksempfaenger.data.SubscriptionListAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

public class SubscriptionListActivity extends BaseActivity {
	DbHelper dbHelper;
	Cursor cursor;
	ListView listTimeline;
	SubscriptionListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscription_list);

		dbHelper = new DbHelper(this);

		listTimeline = (ListView) findViewById(R.id.subscription_list);
	}

	@Override
	protected void onResume() {
		super.onResume();

		cursor = dbHelper.getReadableDatabase().query(DbHelper.Podcast._TABLE,
				null, null, null, null, null, DbHelper.Podcast.TITLE);
		startManagingCursor(cursor);

		adapter = new SubscriptionListAdapter(this, cursor);
		listTimeline.setAdapter(adapter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dbHelper.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.subscription_list, menu);
		addGlobalMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_add:
			startActivity(new Intent(this, AddSubscriptionActivity.class));
			break;
		default:
			handleGlobalMenu(item);
		}
		return true;
	}
}