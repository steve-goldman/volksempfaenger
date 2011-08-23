package net.x4a42.volksempfaenger.ui;

import java.io.File;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.DbHelper;
import net.x4a42.volksempfaenger.data.EpisodeListAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ViewSubscriptionActivity extends BaseActivity implements
		OnItemClickListener {
	private long id;
	private DbHelper dbHelper;

	private ImageView podcastLogo;
	private TextView podcastTitle;
	private TextView podcastDescription;
	private ListView episodeList;
	private Cursor cursor;
	private EpisodeListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Check if there is an ID
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			finish();
			return;
		}
		id = extras.getLong("id");
		if (id <= 0) {
			finish();
			return;
		}

		setContentView(R.layout.view_subscription);

		dbHelper = new DbHelper(this);

		podcastLogo = (ImageView) findViewById(R.id.podcast_logo);
		podcastTitle = (TextView) findViewById(R.id.podcast_title);
		podcastDescription = (TextView) findViewById(R.id.podcast_description);
		episodeList = (ListView) findViewById(R.id.episode_list);
		episodeList.setOnItemClickListener(this);

		cursor = dbHelper.getReadableDatabase().query(DbHelper.Episode._TABLE,
				null, String.format("%s = ?", DbHelper.Episode.PODCAST),
				new String[] { String.valueOf(id) }, null, null,
				String.format("%s DESC", DbHelper.Episode.DATE));
		startManagingCursor(cursor);

		adapter = new EpisodeListAdapter(this, cursor);
		episodeList.setAdapter(adapter);

		File podcastLogoFile = Utils.getPodcastLogoFile(this, id);
		if (podcastLogoFile.isFile()) {
			Bitmap podcastLogoBitmap = BitmapFactory.decodeFile(podcastLogoFile
					.getAbsolutePath());
			podcastLogo.setImageBitmap(podcastLogoBitmap);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Update podcast information
		Cursor c = dbHelper.getReadableDatabase().query(
				DbHelper.Podcast._TABLE, null,
				String.format("%s = ?", DbHelper.Podcast.ID),
				new String[] { String.valueOf(id) }, null, null, null, "1");

		if (c.getCount() == 0) {
			// ID does not exist
			finish();
			return;
		}

		c.moveToFirst();

		podcastTitle.setText(c.getString(c
				.getColumnIndex(DbHelper.Podcast.TITLE)));
		podcastDescription.setText(c.getString(c
				.getColumnIndex(DbHelper.Podcast.DESCRIPTION)));

		c.close();

		// Update episode list
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
		inflater.inflate(R.menu.view_subscription, menu);
		addGlobalMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.item_edit:
			intent = new Intent(this, EditSubscriptionActivity.class);
			intent.putExtra("id", id);
			startActivity(intent);
			return true;
		case R.id.item_delete:
			intent = new Intent(this, DeleteSubscriptionActivity.class);
			intent.putExtra("id", id);
			startActivity(intent);
			return true;
		default:
			return handleGlobalMenu(item);
		}
	}

	public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
		Intent intent = new Intent(this, ViewEpisodeActivity.class);
		intent.putExtra("id", id);
		startActivity(intent);
	}
}
