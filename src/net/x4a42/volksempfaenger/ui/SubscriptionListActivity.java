package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.DatabaseHelper;
import net.x4a42.volksempfaenger.data.SubscriptionListAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

	private DatabaseHelper dbHelper;
	private Cursor cursor;
	private ListView subscriptionList;
	private SubscriptionListAdapter adapter;
	private AdapterView.AdapterContextMenuInfo currentMenuInfo;

	@Override
	// TODO Auto-generated method stub
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscription_list);

		dbHelper = DatabaseHelper.getInstance(this);

		subscriptionList = (ListView) findViewById(R.id.subscription_list);
		subscriptionList
				.setEmptyView(findViewById(R.id.subscription_list_empty));
		subscriptionList.setOnItemClickListener(this);
		subscriptionList.setOnCreateContextMenuListener(this);

		cursor = dbHelper.getReadableDatabase().query(
				DatabaseHelper.ExtendedPodcast._TABLE, null, null, null, null, null,
				DatabaseHelper.ExtendedPodcast.TITLE);
		startManagingCursor(cursor);

		adapter = new SubscriptionListAdapter(this, cursor);
		subscriptionList.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		cursor.requery();
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
			return true;
		default:
			return handleGlobalMenu(item);
		}
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
		currentMenuInfo = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		Intent intent;
		switch (item.getItemId()) {
		case CONTEXT_EDIT:
			intent = new Intent(this, EditSubscriptionActivity.class);
			intent.putExtra("id", currentMenuInfo.id);
			startActivity(intent);
			return true;
		case CONTEXT_DELETE:
			intent = new Intent(this, DeleteSubscriptionActivity.class);
			intent.putExtra("id", currentMenuInfo.id);
			startActivity(intent);
			return true;
		}
		return false;
	}

	public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
		Intent intent = new Intent(this, ViewSubscriptionActivity.class);
		intent.putExtra("id", id);
		startActivity(intent);
	}
}
