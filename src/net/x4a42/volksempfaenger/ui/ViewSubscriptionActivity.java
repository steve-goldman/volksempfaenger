package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.DbHelper;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewSubscriptionActivity extends BaseActivity {
	private long id;
	private DbHelper dbHelper;

	private ImageView podcastLogo;
	private TextView podcastTitle;
	private TextView podcastDescription;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Check if there is an ID
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			finish();
		}
		id = extras.getLong("id");
		if (id <= 0) {
			finish();
		}

		setContentView(R.layout.view_subscription);

		dbHelper = new DbHelper(this);
		Cursor c = dbHelper.getReadableDatabase().query(
				DbHelper.Podcast._TABLE, null,
				String.format("%s = ?", DbHelper.Podcast.ID),
				new String[] { String.valueOf(id) }, null, null, null, "1");

		if (c.getCount() <= 0) {
			// ID does not exist
			finish();
		}

		podcastLogo = (ImageView) findViewById(R.id.podcast_logo);
		podcastTitle = (TextView) findViewById(R.id.podcast_title);
		podcastDescription = (TextView) findViewById(R.id.podcast_description);

		c.moveToFirst();
		podcastTitle.setText(c.getString(c
				.getColumnIndex(DbHelper.Podcast.TITLE)));
		// podcastUrl.setText(c.getString(c.getColumnIndex(DbHelper.Podcast.URL)));
		podcastDescription.setText(c.getString(c
				.getColumnIndex(DbHelper.Podcast.DESCRIPTION)));
	}
}
