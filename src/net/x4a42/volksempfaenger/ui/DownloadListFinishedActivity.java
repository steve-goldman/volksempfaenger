package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.DownloadListFinishedAdapter;
import net.x4a42.volksempfaenger.data.DownloadListRunningAdapter;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

public class DownloadListFinishedActivity extends BaseActivity {

	private Cursor cursor;
	private DownloadListFinishedAdapter adapter;
	private DownloadManager dm;

	private ListView runningList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_list_finished);

		runningList = (ListView) findViewById(R.id.download_list);
		runningList.setEmptyView(findViewById(R.id.download_list_empty));

		dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		Query downloadQuery = new DownloadManager.Query();
		downloadQuery.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL
				| DownloadManager.STATUS_FAILED);
		cursor = dm.query(downloadQuery);
		startManagingCursor(cursor);

		adapter = new DownloadListFinishedAdapter(this, cursor);
		runningList.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		cursor.requery();
	}

}
