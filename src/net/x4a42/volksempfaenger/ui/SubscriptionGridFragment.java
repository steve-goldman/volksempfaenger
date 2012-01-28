package net.x4a42.volksempfaenger.ui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import net.x4a42.volksempfaenger.Constants;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.DatabaseHelper;
import net.x4a42.volksempfaenger.data.SubscriptionListAdapter;
import net.x4a42.volksempfaenger.feedparser.OpmlParser;
import net.x4a42.volksempfaenger.feedparser.SubscriptionTree;
import net.x4a42.volksempfaenger.service.UpdateService;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class SubscriptionGridFragment extends Fragment implements
		OnItemClickListener {

	private static final int CONTEXT_EDIT = 0;
	private static final int CONTEXT_DELETE = 1;

	private static final int PICK_FILE_REQUEST = 0;

	private DatabaseHelper dbHelper;
	private Cursor cursor;
	private GridView subscriptionList;
	private SubscriptionListAdapter adapter;
	private AdapterView.AdapterContextMenuInfo currentMenuInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		dbHelper = DatabaseHelper.getInstance(getActivity());

		cursor = dbHelper.getReadableDatabase().query(
				DatabaseHelper.ExtendedPodcast._TABLE, null, null, null, null,
				null, DatabaseHelper.ExtendedPodcast.TITLE);
		getActivity().startManagingCursor(cursor);

		adapter = new SubscriptionListAdapter(getActivity(), cursor);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.subscription_list, container,
				false);

		subscriptionList = (GridView) view.findViewById(R.id.subscription_list);
		subscriptionList.setEmptyView(view
				.findViewById(R.id.subscription_list_empty));
		subscriptionList.setOnItemClickListener(this);
		subscriptionList.setOnCreateContextMenuListener(this);

		subscriptionList.setAdapter(adapter);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		cursor.requery();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.subscription_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_add:
			startActivity(new Intent(getActivity(),
					AddSubscriptionActivity.class));
			return true;
		case R.id.item_update:
			getActivity().startService(
					new Intent(getActivity(), UpdateService.class));
			Toast.makeText(getActivity(), R.string.message_update_started,
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.import_items:
			Intent intent = new Intent(Constants.ACTION_OI_PICK_FILE);
			intent.putExtra(Constants.EXTRA_OI_TITLE,
					getString(R.string.dialog_choose_opml));
			startActivityForResult(intent, PICK_FILE_REQUEST);
			return true;
		default:
			return false;
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
			intent = new Intent(getActivity(), EditSubscriptionActivity.class);
			intent.putExtra("id", currentMenuInfo.id);
			startActivity(intent);
			return true;
		case CONTEXT_DELETE:
			intent = new Intent(getActivity(), DeleteSubscriptionActivity.class);
			intent.putExtra("id", currentMenuInfo.id);
			startActivity(intent);
			return true;
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PICK_FILE_REQUEST:
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					importFile(data.getData().getPath());
				}
			}
			break;
		}
	}

	public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
		Intent intent = new Intent(getActivity(),
				ViewSubscriptionActivity.class);
		intent.putExtra("id", id);
		startActivity(intent);
	}

	private void importFile(String path) {
		SubscriptionTree tree;
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(path);
			InputStreamReader in = new InputStreamReader(fstream);
			tree = OpmlParser.parse(new BufferedReader(in));
			// TODO remove debug output
			for (SubscriptionTree node : tree) {
				if (node.isFolder()) {
					Log.d("OPML Import", String.valueOf(node.getDepth()) + " "
							+ node.getTitle());
				} else {
					Log.d("OPML Import", String.valueOf(node.getDepth()) + " "
							+ node.getTitle() + " " + node.getUrl());
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		final ArrayList<SubscriptionTree> items = new ArrayList<SubscriptionTree>();
		for (SubscriptionTree node : tree) {
			if (!node.isFolder()) {
				items.add(node);
			}
		}
		String itemTitles[] = new String[items.size()];
		for (int i = 0; i < items.size(); i++) {
			itemTitles[i] = items.get(i).getTitle();
		}

		AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
		ab.setTitle(getString(R.string.dialog_choose_import_feeds));
		OnMultiChoiceClickListener listener = new OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which,
					boolean isChecked) {
				// TODO
			}
		};
		ab.setMultiChoiceItems(itemTitles, null, listener);
		ab.setPositiveButton(getString(R.string.button_import),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO import now!
					}
				});
		ab.show();
	}
}
