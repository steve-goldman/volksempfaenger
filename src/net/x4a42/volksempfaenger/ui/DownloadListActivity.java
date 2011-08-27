package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.DownloadListAdapter;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class DownloadListActivity extends BaseActivity {
	private Cursor cursor;
	private ListView downloadList;
	private DownloadListAdapter adapter;
	private DownloadManager dm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_list);

		downloadList = (ListView) findViewById(R.id.download_list);

		downloadList.setEmptyView(findViewById(R.id.download_list_empty));

		dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		Query downloadQuery = new DownloadManager.Query();
		downloadQuery
				.setFilterByStatus(DownloadManager.STATUS_PENDING
						| DownloadManager.STATUS_RUNNING
						| DownloadManager.STATUS_PAUSED
						| DownloadManager.STATUS_FAILED);
		cursor = dm.query(downloadQuery);
		startManagingCursor(cursor);

		adapter = new DownloadListAdapter(this, cursor);
		downloadList.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		cursor.requery();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

}
