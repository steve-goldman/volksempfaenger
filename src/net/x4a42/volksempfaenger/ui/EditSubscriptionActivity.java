package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.DbHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EditSubscriptionActivity extends BaseActivity implements
		OnClickListener {
	private ImageView podcastLogo;
	private EditText podcastTitle;
	private EditText podcastUrl;
	private EditText podcastDescription;
	private Button buttonSave;
	private Button buttonCancel;

	private long id;
	private DbHelper dbHelper;

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

		setContentView(R.layout.edit_subscription);

		podcastLogo = (ImageView) findViewById(R.id.podcast_logo);
		podcastTitle = (EditText) findViewById(R.id.podcast_title);
		podcastUrl = (EditText) findViewById(R.id.podcast_url);
		podcastDescription = (EditText) findViewById(R.id.podcast_description);
		buttonSave = (Button) findViewById(R.id.button_save);
		buttonCancel = (Button) findViewById(R.id.button_cancel);

		buttonSave.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);

		dbHelper = new DbHelper(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

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
		podcastUrl.setText(c.getString(c.getColumnIndex(DbHelper.Podcast.URL)));
		podcastDescription.setText(c.getString(c
				.getColumnIndex(DbHelper.Podcast.DESCRIPTION)));

		c.close();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		dbHelper.close();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_save:
			ContentValues values = new ContentValues();
			values.put(DbHelper.Podcast.TITLE, podcastTitle.getText()
					.toString());
			values.put(DbHelper.Podcast.URL, podcastUrl.getText().toString());
			values.put(DbHelper.Podcast.DESCRIPTION, podcastDescription
					.getText().toString());
			try {
				int result = dbHelper.getWritableDatabase().update(
						DbHelper.Podcast._TABLE, values,
						String.format("%s = ?", DbHelper.Podcast.ID),
						new String[] { String.valueOf(id) });
				if (result > 0) {
					finish();
					return;
				}
			} catch (Exception e) {
				// Something failed
				if (e instanceof SQLiteConstraintException) {
					// UNIQUE contraint on column url failed
					Toast.makeText(this,
							R.string.message_podcast_already_added,
							Toast.LENGTH_LONG).show();
				} else {
					// Some terrible failure happended
					Log.wtf(getClass().getName(), e);
				}
			}
			return;
		case R.id.button_cancel:
			finish();
			return;
		}
	}
}
