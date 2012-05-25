package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.net.FeedDownloader;
import net.x4a42.volksempfaenger.net.LogoDownloader;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditSubscriptionActivity extends Activity implements
		OnClickListener {

	private static final int CONTEXT_RELOAD = 0;
	private static final int CONTEXT_DELETE = 1;
	private static final int CONTEXT_CHOOSE = 2;

	private PodcastLogoView podcastLogo;
	private EditText podcastTitle;
	private EditText podcastUrl;
	private EditText podcastDescription;
	private Button buttonSave;
	private Button buttonCancel;

	private long id;
	private Uri uri;

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

		setContentView(R.layout.edit_subscription);

		podcastLogo = (PodcastLogoView) findViewById(R.id.podcast_logo);
		podcastTitle = (EditText) findViewById(R.id.podcast_title);
		podcastUrl = (EditText) findViewById(R.id.podcast_url);
		podcastDescription = (EditText) findViewById(R.id.podcast_description);
		buttonSave = (Button) findViewById(R.id.button_save);
		buttonCancel = (Button) findViewById(R.id.button_cancel);

		podcastLogo.setOnClickListener(this);
		buttonSave.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);

		String[] projection = null; // TODO
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (!cursor.moveToFirst()) {
			// ID does not exist
			finish();
			return;
		}
		podcastTitle.setText(cursor.getString(cursor
				.getColumnIndex(Podcast.TITLE)));
		podcastUrl
				.setText(cursor.getString(cursor.getColumnIndex(Podcast.FEED)));
		podcastDescription.setText(cursor.getString(cursor
				.getColumnIndex(Podcast.DESCRIPTION)));
		cursor.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		ExternalStorageHelper.assertExternalStorageWritable(this);
		registerForContextMenu(podcastLogo);
		reloadLogo();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(R.string.title_context_change_logo);

		menu.add(0, CONTEXT_RELOAD, 0, R.string.context_reload);
		menu.add(0, CONTEXT_DELETE, 0, R.string.context_delete);
		menu.add(0, CONTEXT_CHOOSE, 0, R.string.context_choose);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CONTEXT_RELOAD:
			new ReloadLogoTask().execute();
			return true;
		case CONTEXT_DELETE:
			Utils.getPodcastLogoFile(this, id).delete();
			reloadLogo();
			return true;
		case CONTEXT_CHOOSE:
			// TODO
			return true;
		}
		return false;
	}

	private void reloadLogo() {
		podcastLogo.setPodcastId(id);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.podcast_logo:
			openContextMenu(v);
			return;
		case R.id.button_save:
			ContentValues values = new ContentValues();
			values.put(Podcast.TITLE, podcastTitle.getText().toString());
			values.put(Podcast.FEED, podcastUrl.getText().toString());
			values.put(Podcast.DESCRIPTION, podcastDescription.getText()
					.toString());
			try {
				int result = getContentResolver().update(uri, values, null,
						null);
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
					Log.wtf(this, e);
				}
			}
			return;
		case R.id.button_cancel:
			finish();
			return;
		}
	}

	private class ReloadLogoTask extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {

			dialog = ProgressDialog.show(EditSubscriptionActivity.this,
					getString(R.string.dialog_reload_logo_progress_title),
					getString(R.string.dialog_reload_logo_progress_message),
					true);
			dialog.show();

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			String feedUrl = podcastUrl.getText().toString();
			FeedDownloader fd = new FeedDownloader(
					EditSubscriptionActivity.this);
			try {
				Feed feed = fd.fetchFeed(feedUrl);
				String feedImage = feed.image;
				if (feedImage != null) {
					// Try to download podcast logo
					LogoDownloader ld = new LogoDownloader(
							EditSubscriptionActivity.this);

					ld.fetchLogo(feedImage, id);
				}
			} catch (Exception e) {
				Log.w(this, e);
				return false;
			}
			// It didn't fail
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			if (!result) {
				Toast.makeText(EditSubscriptionActivity.this,
						R.string.message_error, Toast.LENGTH_SHORT).show();
			}
			reloadLogo();
		}

	}
}
