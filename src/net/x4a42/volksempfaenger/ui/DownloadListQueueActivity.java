package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
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

	private ListView runningList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_list_running);

		dbHelper = DatabaseHelper.getInstance(this);

		runningList = (ListView) findViewById(R.id.download_list);
		runningList.setEmptyView(findViewById(R.id.download_list_empty));

		cursor = dbHelper.getReadableDatabase()
				.query(DatabaseHelper.ExtendedEpisode._TABLE,
						null,
						String.format("%s = ?",
								DatabaseHelper.ExtendedEpisode.EPISODE_STATE),
						new String[] { String
								.valueOf(DatabaseHelper.Episode.STATE_NEW) },
						null, null, null);

		adapter = new DownloadListQueueAdapter(this, cursor);
		runningList.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		cursor.requery();
	}

}
