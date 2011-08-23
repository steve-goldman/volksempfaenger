package net.x4a42.volksempfaenger.ui;

import java.io.File;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.DatabaseHelper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewEpisodeActivity extends BaseActivity {

	private long id;
	private DatabaseHelper dbHelper;

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

		dbHelper = new DatabaseHelper(this);

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

		c = dbHelper.getReadableDatabase().query(DatabaseHelper.Episode._TABLE, null,
				String.format("%s = ?", DatabaseHelper.Episode.ID),
				new String[] { String.valueOf(id) }, null, null, null);

		if (!c.moveToFirst()) {
			// ID does not exist
			finish();
			return;
		}

		long podcastId = c.getLong(c.getColumnIndex(DatabaseHelper.Episode.PODCAST));
		episodeTitle.setText(c.getString(c
				.getColumnIndex(DatabaseHelper.Episode.TITLE)));
		String description = Utils.normalizeString(c.getString(c
				.getColumnIndex(DatabaseHelper.Episode.DESCRIPTION)));
		Log.d(getClass().getSimpleName(),
				"description length: " + description.length());
		episodeDescription.setText(Html.fromHtml(description));

		c.close();

		File podcastLogoFile = Utils.getPodcastLogoFile(this, podcastId);
		if (podcastLogoFile.isFile()) {
			Bitmap podcastLogoBitmap = BitmapFactory.decodeFile(podcastLogoFile
					.getAbsolutePath());
			podcastLogo.setImageBitmap(podcastLogoBitmap);
		}

		// Update podcast information
		c = dbHelper.getReadableDatabase().query(DatabaseHelper.Podcast._TABLE, null,
				String.format("%s = ?", DatabaseHelper.Podcast.ID),
				new String[] { String.valueOf(podcastId) }, null, null, null,
				"1");

		if (!c.moveToFirst()) {
			// ID does not exist
			finish();
			return;
		}

		podcastTitle.setText(c.getString(c
				.getColumnIndex(DatabaseHelper.Podcast.TITLE)));
		podcastDescription.setText(c.getString(c
				.getColumnIndex(DatabaseHelper.Podcast.DESCRIPTION)));

		c.close();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
