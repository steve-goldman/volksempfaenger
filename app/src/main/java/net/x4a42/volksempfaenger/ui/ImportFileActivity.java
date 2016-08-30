package net.x4a42.volksempfaenger.ui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.feedparser.OpmlParser;
import net.x4a42.volksempfaenger.feedparser.SubscriptionTree;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ImportFileActivity extends Activity implements
		OnItemClickListener, OnClickListener {
	public static final String EXTRA_IMPORT_FILE_PATH = "IMPORT_FILE_PATH";
	public static final String EXTRA_IMPORT_FAILED_ITEMS = "IMPORT_FAILED_ITEMS";
	private ListView mListView;
	private RelativeLayout mLoadingIndicator;
	ArrayList<SubscriptionTree> items;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.dialog_choose_import_feeds);
		setContentView(R.layout.import_file);

		mListView = (ListView) findViewById(R.id.import_list);
		mLoadingIndicator = (RelativeLayout) findViewById(R.id.loading);

		final Button importButton = (Button) findViewById(R.id.button_import);
		final Button cancelButton = (Button) findViewById(R.id.button_cancel);
		importButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);

		mListView.setOnItemClickListener(this);
		final String filename = getIntent().getData() != null ? getIntent()
				.getData().getPath() : getIntent().getStringExtra(
				EXTRA_IMPORT_FILE_PATH);
		if (filename == null) {
			finish();
			return;
		}

		new LoadFileTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
				filename);
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View view, int position,
			long id) {
		final boolean checked = mListView.isItemChecked(position);
		final SubscriptionTree item = items.get(position);
		if (item.isFolder()) {
			for (SubscriptionTree child : item) {
				mListView.setItemChecked(child.id, checked);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_cancel:
			finish();
			break;
		case R.id.button_import:
			final SparseBooleanArray checked = mListView
					.getCheckedItemPositions();
			SubscriptionTree[] checkedItems = new SubscriptionTree[checked
					.size()];
			for (int i = 0; i < checked.size(); i++) {
				checkedItems[i] = items.get(checked.keyAt(i));
			}
			new ImportTask(getApplicationContext()).executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, checkedItems);
			Toast.makeText(getApplicationContext(),
					R.string.message_import_started, Toast.LENGTH_SHORT).show();
			finish();
			break;
		}
	}

	private class LoadFileTask extends
			AsyncTask<String, Void, SubscriptionTree> {
		private String path;
		private String errorTitle, errorMessage;

		@Override
		protected SubscriptionTree doInBackground(String... params) {
			SubscriptionTree tree;
			FileInputStream fstream;
			path = params[0];
			try {
				fstream = new FileInputStream(path);
				InputStreamReader in = new InputStreamReader(fstream);
				tree = OpmlParser.parse(new BufferedReader(in));
				for (SubscriptionTree node : tree) {
					if (node.isFolder()) {
						Log.v(this, String.valueOf(node.depth) + " "
								+ node.title);
					} else {
						Log.v(this, String.valueOf(node.depth) + " "
								+ node.title + " " + node.url);
					}
				}
			} catch (NullPointerException e) {
				errorTitle = getString(R.string.title_import_error);
				errorMessage = getString(R.string.message_error_import_unexpected);
				return null;
			} catch (FileNotFoundException e) {
				errorTitle = getString(R.string.title_file_not_found);
				errorMessage = String.format(
						getString(R.string.message_error_file_not_found), path);
				return null;
			}
			return tree;
		}

		@Override
		protected void onPostExecute(SubscriptionTree tree) {
			if (tree == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ImportFileActivity.this);
				builder.setTitle(errorTitle).setMessage(errorMessage)
						.setCancelable(false);
				builder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								finish();
							}
						});
				builder.create().show();
				return;
			}
			items = new ArrayList<SubscriptionTree>();
			for (SubscriptionTree node : tree) {
				items.add(node);
			}
			final String itemTitles[] = new String[items.size()];
			for (int i = 0; i < items.size(); i++) {
				final SubscriptionTree item = items.get(i);
				String spacing = "";
				String symbol = "";
				for (int j = 1; j < item.depth; j++) {
					spacing = spacing + "\t";
				}
				if (item.isFolder()) {
					symbol = "▼ ";
				}
				itemTitles[i] = spacing + symbol + item.title;
				item.id = i;
			}

			final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					ImportFileActivity.this, R.layout.import_file_row,
					itemTitles);
			mListView.setAdapter(adapter);
			mListView.setVisibility(View.VISIBLE);
			mLoadingIndicator.setVisibility(View.GONE);
		}
	}
}
