package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.DatabaseHelper;
import net.x4a42.volksempfaenger.data.DownloadListQueueAdapter;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

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

		cursor = dbHelper
				.getReadableDatabase()
				.rawQuery(
						"SELECT enclosure._id AS _id, podcast._id AS podcast_id, "
								+ "podcast.title AS podcast_title, episode.title AS "
								+ "episode_title FROM enclosure JOIN episode ON "
								+ "episode._id = enclosure.episode_id JOIN podcast "
								+ "ON podcast._id = episode.podcast_id WHERE "
								+ "enclosure.state = ? ORDER BY episode.date DESC",
						new String[] { String
								.valueOf(DatabaseHelper.Enclosure.STATE_NEW) });

		adapter = new DownloadListQueueAdapter(this, cursor);
		runningList.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		cursor.requery();
	}

}
