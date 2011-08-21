package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.DbHelper;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddSubscriptionActivity extends BaseActivity implements
		OnClickListener {
	private EditText editTextUrl;
	private Button buttonAdd;
	private Button buttonCancel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_subscription);

		LayoutParams params = getWindow().getAttributes();
		params.width = LayoutParams.FILL_PARENT;
		// params.flags = LayoutParams.FLAG_BLUR_BEHIND;
		getWindow().setAttributes(params);

		editTextUrl = (EditText) findViewById(R.id.edittext_url);
		buttonAdd = (Button) findViewById(R.id.button_add);
		buttonCancel = (Button) findViewById(R.id.button_cancel);

		buttonAdd.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_add:
			subscribeToFeed();
			break;
		case R.id.button_cancel:
			finish();
			break;
		}
	}

	private void subscribeToFeed() {
		String feedUrl = editTextUrl.getText().toString();
		
		// TODO: check feed url and fetch real data

		// Open database
		DbHelper dbHelper = new DbHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(DbHelper.Podcast.TITLE, feedUrl);
		values.put(DbHelper.Podcast.DESCRIPTION, "Dummy description");
		values.put(DbHelper.Podcast.URL, feedUrl);
		values.put(DbHelper.Podcast.WEBSITE, "http://example.com/");
		
		try {
			// Try to add the podcast to the database
			db.insertOrThrow(DbHelper.Podcast._TABLE, null, values);
			// Succeeded. Display message and exit
			Toast.makeText(this, R.string.message_podcast_successfully_added,
					Toast.LENGTH_SHORT).show();
			finish();
		} catch (SQLException e) {
			// Something failed
			if (e instanceof SQLiteConstraintException) {
				// UNIQUE contraint on column url failed
				Toast.makeText(this, R.string.message_podcast_already_added,
						Toast.LENGTH_LONG).show();
			} else {
				// Some terrible failure happended
				Log.wtf(getClass().getName(), e);
			}
		} finally {
			dbHelper.close();
		}
	}
}
