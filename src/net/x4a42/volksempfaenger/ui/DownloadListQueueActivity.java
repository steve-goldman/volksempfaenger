package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.DatabaseHelper;
import net.x4a42.volksempfaenger.data.DownloadListQueueAdapter;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

// TODO: Improve implementation. GC has a lot of work to do in this activity

public class DownloadListQueueActivity extends BaseActivity {

	private DatabaseHelper dbHelper;
	private Cursor cursor;
	private DownloadListQueueAdapter adapter;

	private ListView queueList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_list_queue);

		dbHelper = DatabaseHelper.getInstance(this);

		queueList = (ListView) findViewById(R.id.download_list);
		queueList.setEmptyView(findViewById(R.id.download_list_empty));

		String selection = DatabaseHelper.ExtendedEpisode.ENCLOSURE_URL
				+ " IS NOT NULL  AND "
				+ DatabaseHelper.ExtendedEpisode.EPISODE_STATE + " = "
				+ DatabaseHelper.Episode.STATE_NEW;

		String orderBy = String.format("%s DESC",
				DatabaseHelper.ExtendedEpisode.EPISODE_DATE);

		cursor = dbHelper.getReadableDatabase().query(
				DatabaseHelper.ExtendedEpisode._TABLE, null, selection, null,
				null, null, orderBy);

		adapter = new DownloadListQueueAdapter(this, cursor);
		queueList.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		cursor.requery();
	}

}
