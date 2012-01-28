package net.x4a42.volksempfaenger.ui;

import java.io.File;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.service.UpdateService;
import android.app.ActionBar;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ViewSubscriptionActivity extends FragmentActivity implements
		OnItemClickListener {

	private static Map<Integer, Integer> rowColorMap;
	private static final String PODCAST_WHERE = Podcast._ID + "=?";
	private static final String EPISODE_WHERE = Episode.PODCAST_ID + "=?";
	private static final String EPISODE_SORT = Episode.DATE + " DESC, "
			+ Episode._ID + " DESC";

	private long id;

	private ImageView podcastLogo;
	private TextView podcastDescription;
	private ListView episodeList;
	private Cursor episodeCursor;
	private Cursor podcastCursor;
	private Adapter adapter;

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

		initRowColorMap();

		setContentView(R.layout.view_subscription);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		podcastLogo = (ImageView) findViewById(R.id.podcast_logo);
		podcastDescription = (TextView) findViewById(R.id.podcast_description);
		episodeList = (ListView) findViewById(R.id.episode_list);
		episodeList.setOnItemClickListener(this);

		// Update podcast information
		podcastCursor = managedQuery(ContentUris.withAppendedId(
				VolksempfaengerContentProvider.PODCAST_URI, id),
				new String[] {/* TODO */}, PODCAST_WHERE,
				new String[] { String.valueOf(id) }, null);

		if (podcastCursor.getCount() == 0) {
			// ID does not exist
			finish();
			return;
		}

		episodeCursor = managedQuery(
				VolksempfaengerContentProvider.EPISODE_URI, new String[] {
						Episode._ID, Episode.TITLE, Episode.DATE,
						Episode.STATUS }, EPISODE_WHERE,
				new String[] { String.valueOf(id) }, EPISODE_SORT);

		adapter = new Adapter(episodeCursor);
		episodeList.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		podcastCursor.moveToFirst();

		setTitle(podcastCursor.getString(podcastCursor
				.getColumnIndex(Podcast.TITLE)));
		updatePodcastDescription(podcastCursor.getString(podcastCursor
				.getColumnIndex(Podcast.DESCRIPTION)));

		File podcastLogoFile = Utils.getPodcastLogoFile(this, id);
		if (podcastLogoFile.isFile()) {
			Bitmap podcastLogoBitmap = BitmapFactory.decodeFile(podcastLogoFile
					.getAbsolutePath());
			podcastLogo.setImageBitmap(podcastLogoBitmap);
		}
	}

	private void updatePodcastDescription(String description) {
		podcastDescription.setText(description);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_subscription, menu);
		BaseActivity.addGlobalMenu(this, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
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
			return BaseActivity.handleGlobalMenu(this, item);
		}
	}

	public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
		Intent intent = new Intent(this, ViewEpisodeActivity.class);
		intent.putExtra("id", id);
		startActivity(intent);
	}

	private void initRowColorMap() {
		if (rowColorMap != null) {
			return;
		}
		Resources res = getResources();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		map.put(Constants.EPISODE_STATE_NEW,
				res.getColor(R.color.episode_title_new));
		map.put(Constants.EPISODE_STATE_DOWNLOADING,
				res.getColor(R.color.episode_title_downloading));
		map.put(Constants.EPISODE_STATE_READY,
				res.getColor(R.color.episode_title_ready));
		map.put(Constants.EPISODE_STATE_LISTENING,
				res.getColor(R.color.episode_title_listening));
		map.put(Constants.EPISODE_STATE_LISTENED,
				res.getColor(R.color.episode_title_listened));
		rowColorMap = Collections.unmodifiableMap(map);
	}

	public class Adapter extends SimpleCursorAdapter {

		public Adapter(Cursor cursor) {
			super(ViewSubscriptionActivity.this,
					R.layout.view_subscription_row, cursor,
					new String[] { Episode.TITLE },
					new int[] { R.id.episode_title });
		}

		@Override
		public void bindView(View row, Context context, Cursor cursor) {
			super.bindView(row, context, cursor);

			int episodeState = cursor.getInt(cursor
					.getColumnIndex(Episode.STATUS));
			TextView episodeTitle = (TextView) row
					.findViewById(R.id.episode_title);
			episodeTitle.setTextColor(rowColorMap.get(episodeState));

			Date date = new Date(cursor.getLong(cursor
					.getColumnIndex(Episode.DATE)) * 1000);

			TextView episodeDate = (TextView) row
					.findViewById(R.id.episode_date);
			episodeDate.setText(DateFormat.getDateInstance().format(date));
		}
	}
}
