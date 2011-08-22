package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.DbHelper;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DeleteSubscriptionActivity extends BaseActivity implements
		OnClickListener {
	private DbHelper dbHelper;
	private long id;

	private TextView textMessage;
	private Button buttonOk;
	private Button buttonCancel;

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

		dbHelper = new DbHelper(this);

		setContentView(R.layout.delete_subscription);

		LayoutParams params = getWindow().getAttributes();
		params.width = LayoutParams.FILL_PARENT;
		getWindow().setAttributes(params);

		textMessage = (TextView) findViewById(R.id.text_message);
		buttonOk = (Button) findViewById(R.id.button_ok);
		buttonCancel = (Button) findViewById(R.id.button_cancel);
	}

	@Override
	protected void onResume() {
		super.onResume();

		Cursor c = dbHelper.getReadableDatabase().query(
				DbHelper.Podcast._TABLE, null,
				String.format("%s = ?", DbHelper.Podcast.ID),
				new String[] { String.valueOf(id) }, null, null, null, "1");

		if (c.getCount() <= 0) {
			// ID does not exist
			finish();
			return;
		}

		c.moveToFirst();

		textMessage.setText(getString(R.string.message_podcast_confirm_delete,
				c.getString(c.getColumnIndex(DbHelper.Podcast.TITLE))));
		buttonOk.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		dbHelper.close();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_ok:
			deletePodcast();
			return;
		case R.id.button_cancel:
			finish();
			return;
		}
	}

	public void deletePodcast() {
		int result = dbHelper.getWritableDatabase().delete(
				DbHelper.Podcast._TABLE,
				String.format("%s = ?", DbHelper.Podcast.ID),
				new String[] { String.valueOf(id) });
		if (result > 0) {
			// row was deleted
			Toast.makeText(this, R.string.message_podcast_successfully_deleted,
					Toast.LENGTH_LONG).show();
		}
		finish();
	}
}
