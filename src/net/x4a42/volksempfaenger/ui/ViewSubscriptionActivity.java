package net.x4a42.volksempfaenger.ui;

import java.io.File;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.DatabaseHelper;
import net.x4a42.volksempfaenger.data.EpisodeListAdapter;
import net.x4a42.volksempfaenger.service.UpdateService;
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
import android.widget.Toast;

public class ViewSubscriptionActivity extends BaseActivity implements
		OnItemClickListener {
	private long id;
	private DatabaseHelper dbHelper;

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

		dbHelper = DatabaseHelper.getInstance(this);

		podcastLogo = (ImageView) findViewById(R.id.podcast_logo);
		podcastTitle = (TextView) findViewById(R.id.podcast_title);
		podcastDescription = (TextView) findViewById(R.id.podcast_description);
		episodeList = (ListView) findViewById(R.id.episode_list);
		episodeList.setOnItemClickListener(this);

		cursor = dbHelper.getReadableDatabase().query(
				DatabaseHelper.ExtendedEpisode._TABLE,
				null,
				String.format("%s = ?",
						DatabaseHelper.ExtendedEpisode.PODCAST_ID),
				new String[] { String.valueOf(id) },
				null,
				null,
				String.format("%s DESC",
						DatabaseHelper.ExtendedEpisode.EPISODE_DATE));
		startManagingCursor(cursor);

		adapter = new EpisodeListAdapter(this, cursor);
		episodeList.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Update podcast information
		Cursor c = dbHelper.getReadableDatabase().query(
				DatabaseHelper.Podcast._TABLE, null,
				String.format("%s = ?", DatabaseHelper.Podcast.ID),
				new String[] { String.valueOf(id) }, null, null, null, "1");

		if (c.getCount() == 0) {
			// ID does not exist
			finish();
			return;
		}

		c.moveToFirst();

		podcastTitle.setText(c.getString(c
				.getColumnIndex(DatabaseHelper.Podcast.TITLE)));
		podcastDescription.setText(c.getString(c
				.getColumnIndex(DatabaseHelper.Podcast.DESCRIPTION)));

		c.close();

		File podcastLogoFile = Utils.getPodcastLogoFile(this, id);
		if (podcastLogoFile.isFile()) {
			Bitmap podcastLogoBitmap = BitmapFactory.decodeFile(podcastLogoFile
					.getAbsolutePath());
			podcastLogo.setImageBitmap(podcastLogoBitmap);
		}

		// Update episode list
		cursor.requery();
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
		case R.id.item_update:
			intent = new Intent(this, UpdateService.class);
			intent.putExtra("id", new long[] { id });
			startService(intent);
			Toast.makeText(this, R.string.message_update_started,
					Toast.LENGTH_SHORT).show();
			return true;
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
