package net.x4a42.volksempfaenger.ui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.PodcastHelper;
import net.x4a42.volksempfaenger.feedparser.OpmlParser;
import net.x4a42.volksempfaenger.feedparser.SubscriptionTree;
import net.x4a42.volksempfaenger.receiver.BackgroundErrorReceiver;
import android.app.Activity;
import android.content.Intent;
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
	private ArrayList<SubscriptionTree> items;

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
		final String filename = getIntent().getStringExtra(
				EXTRA_IMPORT_FILE_PATH);
		if (filename == null) {
			finish();
			return;
		}

		new LoadFileTask().execute(filename);
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
			new ImportTask().execute(checked);
			Toast.makeText(getApplicationContext(),
					R.string.message_import_started, Toast.LENGTH_SHORT).show();
			finish();
			break;
		}
	}

	private class LoadFileTask extends
			AsyncTask<String, Void, SubscriptionTree> {
		String path;

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
			} catch (FileNotFoundException e) {
				return null;
			}
			return tree;
		}

		@Override
		protected void onPostExecute(SubscriptionTree tree) {
			if (tree == null) {
				final String message = String.format(
						getString(R.string.message_error_file_not_found), path);
				ActivityHelper
						.buildErrorDialog(ImportFileActivity.this,
								getString(R.string.title_file_not_found),
								message, null).show();
				finish();
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

	private class ImportTask extends AsyncTask<SparseBooleanArray, Void, Void> {
		final LinkedList<String> failed = new LinkedList<String>();

		@Override
		protected Void doInBackground(SparseBooleanArray... params) {
			final SparseBooleanArray checked = params[0];
			for (int i = 0; i < checked.size(); i++) {
				SubscriptionTree subscription = items.get(checked.keyAt(i));
				// TODO finer grained exception handling
				try {
					PodcastHelper.addFeed(ImportFileActivity.this,
							subscription.url);
				} catch (Exception e) {
					if (subscription == null) {
						continue;
					}
					String name;
					if (subscription.title == null) {
						name = subscription.url;
					} else {
						name = subscription.title;
					}
					failed.add(name);

				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void arg0) {
			if (failed.size() > 0) {
				Intent intent = BackgroundErrorReceiver
						.getBackgroundErrorIntent(
								getString(R.string.title_import_error),
								getString(R.string.message_error_import),
								BackgroundErrorReceiver.ERROR_IMPORT);
				StringBuilder strBuilder = new StringBuilder();
				for (String podcast : failed) {
					strBuilder.append(podcast);
					strBuilder.append("\n");
				}
				String text = strBuilder.substring(0, strBuilder.length() - 1);
				intent.putExtra(EXTRA_IMPORT_FAILED_ITEMS, text);
				sendOrderedBroadcast(intent, null);
			}
		}
	}
}
