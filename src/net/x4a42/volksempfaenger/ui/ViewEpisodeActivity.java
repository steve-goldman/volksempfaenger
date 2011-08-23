package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.DbHelper;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewEpisodeActivity extends BaseActivity {

	private long id;
	private DbHelper dbHelper;

	private ImageView podcastLogo;
	private TextView podcastTitle;
	private TextView podcastDescription;
	private TextView episodeTitle;
	private TextView episodeDescription;

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

		setContentView(R.layout.view_episode);

		dbHelper = new DbHelper(this);

		podcastLogo = (ImageView) findViewById(R.id.podcast_logo);
		podcastTitle = (TextView) findViewById(R.id.podcast_title);
		podcastDescription = (TextView) findViewById(R.id.podcast_description);
		episodeTitle = (TextView) findViewById(R.id.episode_title);
		episodeDescription = (TextView) findViewById(R.id.episode_description);

		episodeDescription.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Cursor c;

		// Update episode information

		c = dbHelper.getReadableDatabase().query(DbHelper.Episode._TABLE, null,
				String.format("%s = ?", DbHelper.Episode.ID),
				new String[] { String.valueOf(id) }, null, null, null);

		if (!c.moveToFirst()) {
			// ID does not exist
			finish();
			return;
		}

		long podcastId = c.getLong(c.getColumnIndex(DbHelper.Episode.PODCAST));
		episodeTitle.setText(c.getString(c
				.getColumnIndex(DbHelper.Episode.TITLE)));
		String description = Utils.normalizeString(c.getString(c
				.getColumnIndex(DbHelper.Episode.DESCRIPTION)));
		Log.d(getClass().getSimpleName(),
				"description length: " + description.length());
		episodeDescription.setText(Html.fromHtml(description));

		c.close();

		// Update podcast information
		c = dbHelper.getReadableDatabase().query(DbHelper.Podcast._TABLE, null,
				String.format("%s = ?", DbHelper.Podcast.ID),
				new String[] { String.valueOf(podcastId) }, null, null, null,
				"1");

		if (!c.moveToFirst()) {
			// ID does not exist
			finish();
			return;
		}

		podcastTitle.setText(c.getString(c
				.getColumnIndex(DbHelper.Podcast.TITLE)));
		podcastDescription.setText(c.getString(c
				.getColumnIndex(DbHelper.Podcast.DESCRIPTION)));

		c.close();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
