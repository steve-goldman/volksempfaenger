package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.DbHelper;
import net.x4a42.volksempfaenger.data.SubscriptionListAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.widget.Toast;

public class SubscriptionListActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener {
	private static final int CONTEXT_EDIT = 0;
	private static final int CONTEXT_DELETE = 1;

	private DbHelper dbHelper;
	private Cursor cursor;
	private ListView subscriptionList;
	private SubscriptionListAdapter adapter;
	private AdapterView.AdapterContextMenuInfo currentMenuInfo;

	@Override
	// TODO Auto-generated method stub
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscription_list);

		dbHelper = new DbHelper(this);

		subscriptionList = (ListView) findViewById(R.id.subscription_list);
		subscriptionList.setOnItemClickListener(this);
		subscriptionList.setOnCreateContextMenuListener(this);
		
		cursor = dbHelper.getReadableDatabase().query(DbHelper.Podcast._TABLE,
				null, null, null, null, null, DbHelper.Podcast.TITLE);
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
		currentMenuInfo = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case CONTEXT_EDIT:
			Intent intent = new Intent(this, EditSubscriptionActivity.class);
			intent.putExtra("id", currentMenuInfo.id);
			startActivity(intent);
			return true;
		case CONTEXT_DELETE:
			deleteSubscription();
			return true;
		}
		return false;
	}

	private void deleteSubscription() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		TextView podcastTitle = (TextView) currentMenuInfo.targetView
				.findViewById(R.id.podcast_title);
		builder.setTitle(R.string.title_confirm_delete)
				.setMessage(
						String.format(
								getString(R.string.message_podcast_confirm_delete),
								podcastTitle.getText().toString()))
				.setCancelable(false).setPositiveButton(R.string.yes, this)
				.setNegativeButton(R.string.no, this);
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
		// TODO Auto-generated method stub
		Log.d(getClass().getSimpleName(), String.format("onItemClick(%d)", id));
	}

	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case Dialog.BUTTON_POSITIVE:
			// TODO: Delete stuff
			int result = dbHelper.getWritableDatabase().delete(
					DbHelper.Podcast._TABLE,
					String.format("%s = ?", DbHelper.Podcast.ID),
					new String[] { String.valueOf(currentMenuInfo.id) });
			if (result > 0) {
				// row was deleted
				Toast.makeText(this,
						R.string.message_podcast_successfully_deleted,
						Toast.LENGTH_LONG).show();
			}
			cursor.requery();
			break;
		case Dialog.BUTTON_NEGATIVE:
			dialog.cancel();
			break;
		}
	}
}