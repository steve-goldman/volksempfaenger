package net.x4a42.volksempfaenger.ui;

import java.io.File;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.DbHelper;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.net.FeedDownloader;
import net.x4a42.volksempfaenger.net.LogoDownloader;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EditSubscriptionActivity extends BaseActivity implements
		OnClickListener {

	private static final int CONTEXT_RELOAD = 0;
	private static final int CONTEXT_DELETE = 1;
	private static final int CONTEXT_CHOOSE = 2;

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

		podcastLogo.setOnClickListener(this);
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

		registerForContextMenu(podcastLogo);
		reloadLogo();

		c.close();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		dbHelper.close();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		menu.setHeaderTitle("Hi");

		menu.add(0, CONTEXT_RELOAD, 0, R.string.context_reload);
		menu.add(0, CONTEXT_DELETE, 0, R.string.context_delete);
		menu.add(0, CONTEXT_CHOOSE, 0, R.string.context_choose);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// menuInfo = (AdapterView.AdapterContextMenuInfo) item
		// .getMenuInfo();
		// Intent intent;
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
		File podcastLogoFile = Utils.getPodcastLogoFile(this, id);
		if (podcastLogoFile.isFile()) {
			Bitmap podcastLogoBitmap = BitmapFactory.decodeFile(podcastLogoFile
					.getAbsolutePath());
			podcastLogo.setImageBitmap(podcastLogoBitmap);
		} else {
			podcastLogo.setImageResource(R.drawable.default_logo);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.podcast_logo:
			openContextMenu(v);
			return;
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
				String feedImage = feed.getImage();
				if (feedImage != null) {
					// Try to download podcast logo
					LogoDownloader ld = new LogoDownloader(
							EditSubscriptionActivity.this);

					ld.fetchLogo(feedImage, id);
				}
			} catch (Exception e) {
				Log.w(getClass().getName(), e);
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
