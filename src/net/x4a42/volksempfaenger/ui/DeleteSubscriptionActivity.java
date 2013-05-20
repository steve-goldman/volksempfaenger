package net.x4a42.volksempfaenger.ui;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns;
import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.EpisodeHelper;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DeleteSubscriptionActivity extends Activity implements
		OnClickListener {

	private long id;
	private Uri uri;

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
		uri = ContentUris.withAppendedId(
				VolksempfaengerContentProvider.PODCAST_URI, id);

		setContentView(R.layout.delete_subscription);

		LayoutParams params = getWindow().getAttributes();
		params.width = LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(params);

		textMessage = (TextView) findViewById(R.id.text_message);
		buttonOk = (Button) findViewById(R.id.button_ok);
		buttonCancel = (Button) findViewById(R.id.button_cancel);
		buttonOk.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);

		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		if (!cursor.moveToFirst()) {
			// ID does not exist
			finish();
			return;
		}
		textMessage.setText(getString(R.string.message_podcast_confirm_delete,
				cursor.getString(cursor.getColumnIndex(Podcast.TITLE))));
		cursor.close();

	}

	@Override
	protected void onResume() {
		super.onResume();
		ExternalStorageHelper.assertExternalStorageReadable(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_ok:
			deletePodcast();
			setResult(RESULT_OK);
			finish();
			return;
		case R.id.button_cancel:
			setResult(RESULT_CANCELED);
			finish();
			return;
		}
	}

	public void deletePodcast() {

		ContentResolver resolver = this.getContentResolver();
		Cursor cursor = resolver.query(
				VolksempfaengerContentProvider.EPISODE_URI,
				new String[] {Columns.Episode._ID, Columns.Episode.DOWNLOAD_BYTES_DOWNLOADED_SO_FAR }, Columns.Episode.PODCAST_ID + "=" + String.valueOf(id), null, null);

		EpisodeCursor episodeCursor = new EpisodeCursor(cursor);

		if(episodeCursor.moveToFirst())
		{
			do{
				if(episodeCursor.getDownloadDone() > 0)
				{
					EpisodeHelper
							.deleteDownload(getContentResolver(),
									(DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE),
									episodeCursor.getId()
									);
				}
			} while (episodeCursor.moveToNext());
		}

		episodeCursor.close();

		int result = getContentResolver().delete(uri, null, null);

		if (result > 0) {
			// row was deleted
			Toast.makeText(this, R.string.message_podcast_successfully_deleted,
					Toast.LENGTH_LONG).show();
		}
		// delete podcast logo
		Utils.getPodcastLogoFile(this, id).delete();
	}
}
