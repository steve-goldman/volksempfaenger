package net.Ox4a42.volksempfaenger.ui;

import net.Ox4a42.volksempfaenger.R;
import net.Ox4a42.volksempfaenger.data.DbHelper;
import net.Ox4a42.volksempfaenger.data.SubscriptionListAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class SubscriptionListActivity extends BaseActivity implements
		OnItemClickListener {
	private static final int CONTEXT_EDIT = 0;
	private static final int CONTEXT_DELETE = 1;

	DbHelper dbHelper;
	Cursor cursor;
	ListView subscriptionList;
	SubscriptionListAdapter adapter;

	@Override
	// TODO Auto-generated method stub
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscription_list);

		dbHelper = new DbHelper(this);

		subscriptionList = (ListView) findViewById(R.id.subscription_list);
		subscriptionList.setOnItemClickListener(this);
		subscriptionList.setOnCreateContextMenuListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		cursor = dbHelper.getReadableDatabase().query(DbHelper.Podcast._TABLE,
				null, null, null, null, null, DbHelper.Podcast.TITLE);
		startManagingCursor(cursor);

		adapter = new SubscriptionListAdapter(this, cursor);
		subscriptionList.setAdapter(adapter);
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		TextView podcastTitle = (TextView) info.targetView
				.findViewById(R.id.podcast_title);
		String title = podcastTitle.getText().toString();
		menu.setHeaderTitle(title);

		menu.add(0, CONTEXT_EDIT, 0, R.string.context_edit);
		menu.add(0, CONTEXT_DELETE, 0, R.string.context_delete);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case CONTEXT_DELETE:
			return true;
		}
		return false;
	}

	public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
		// TODO Auto-generated method stub
		Log.d(getClass().getSimpleName(), String.format("onItemClick(%d)", id));
	}
}