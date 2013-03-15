package net.x4a42.volksempfaenger.ui;

import java.io.File;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.PreferenceKeys;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.data.Columns.Enclosure;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.EpisodeHelper;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.net.DescriptionImageDownloader;
import net.x4a42.volksempfaenger.service.DownloadService;
import net.x4a42.volksempfaenger.service.FlattrService;
import net.x4a42.volksempfaenger.service.PlaybackHelper.Event;
import net.x4a42.volksempfaenger.service.PlaybackHelper.EventListener;
import net.x4a42.volksempfaenger.service.PlaybackService;
import net.x4a42.volksempfaenger.service.PlaybackService.PlaybackBinder;
import net.x4a42.volksempfaenger.service.PlaybackService.PlaybackRemote;

import org.xml.sax.XMLReader;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.Editable;
import android.text.Html;
import android.text.Html.TagHandler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ViewEpisodeActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Cursor>, ServiceConnection,
		EventListener, OnUpPressedCallback, OnClickListener {

	private static final String WHERE_EPISODE_ID = Enclosure.EPISODE_ID + "=?";

	private Uri mEpisodeUri;
	private long mEpisodeId;
	private EpisodeCursor episodeCursor;

	private TextView episodeTitle;
	private TextView episodeMeta;
	private TextView episodeDescription;
	private int descriptionHash;
	private ImageButton flattrButton;

	private AsyncTask<Void, ImageSpan, Void> lastImageLoadTask;
	private SpannableStringBuilder descriptionSpanned;

	private long subscriptionId = -1;

	private PlaybackRemote remote;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_episode);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		episodeTitle = (TextView) findViewById(R.id.episode_title);
		episodeMeta = (TextView) findViewById(R.id.episode_meta);
		episodeDescription = (TextView) findViewById(R.id.episode_description);
		flattrButton = (ImageButton) findViewById(R.id.button_flattr);
		flattrButton.setOnClickListener(this);

		episodeDescription.setMovementMethod(LinkMovementMethod.getInstance());

		bindService(new Intent(this, PlaybackService.class), this,
				Activity.BIND_AUTO_CREATE);

		mEpisodeUri = getIntent().getData();

		if (mEpisodeUri == null) {
			mEpisodeId = getIntent().getLongExtra("id", -1);
			if (mEpisodeId == -1) {
				finish();
				return;
			}
			mEpisodeUri = ContentUris.withAppendedId(
					VolksempfaengerContentProvider.EPISODE_URI, mEpisodeId);
		} else {
			mEpisodeId = ContentUris.parseId(mEpisodeUri);
		}

		LoaderManager lm = getLoaderManager();
		if (lm.getLoader(0) == null) {
			lm.initLoader(0, null, this);
		} else {
			lm.restartLoader(0, null, this);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		ExternalStorageHelper.assertExternalStorageWritable(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_episode, menu);

		if (episodeCursor != null && remote != null) {
			MenuItem downloadItem = menu.findItem(R.id.item_download);
			MenuItem playItem = menu.findItem(R.id.item_play);
			MenuItem markListenedItem = menu.findItem(R.id.item_mark_listened);
			MenuItem markNewItem = menu.findItem(R.id.item_mark_new);

			int status = episodeCursor.getStatus();

			if ((episodeCursor.getDownloadStatus() != DownloadManager.STATUS_FAILED && episodeCursor
					.getDownloadStatus() != -1)
					|| episodeCursor.getEnclosureNumber() == 0) {
				downloadItem.setVisible(false);
			} else {
				downloadItem.setVisible(true);
			}

			if ((remote.isPlaying() && mEpisodeUri.equals(remote
					.getEpisodeUri()))
					|| (episodeCursor.getEnclosureNumber() == 0)) {
				playItem.setVisible(false);
			} else {
				playItem.setVisible(true);
			}

			if (!EpisodeHelper.canMarkAsListened(status)) {
				markListenedItem.setVisible(false);
			} else {
				markListenedItem.setVisible(true);
			}

			if (!EpisodeHelper.canMarkAsNew(status)) {
				markNewItem.setVisible(false);
			} else {
				markNewItem.setVisible(true);
			}

		}

		ActivityHelper.addGlobalMenu(this, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.item_play:
			Intent intent = new Intent(this, PlaybackService.class);
			intent.setAction(PlaybackService.ACTION_PLAY);
			intent.setData(mEpisodeUri);
			startService(intent);
			return true;

		case R.id.item_download:
			if (episodeCursor.getEnclosureId() != 0) {
				// there is an preferred enclosure
				downloadEnclosure();
			} else {
				final EnclosureSimple[] enclosures = getEnclosures();
				switch (enclosures.length) {
				case 0:
					// no enclosures
					Toast.makeText(this,
							R.string.message_episode_without_enclosure,
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					// exactly one enclosure
					downloadEnclosure(enclosures[0].id);
					break;
				default:
					// multiple enclosures (they suck)
					AlertDialog dialog = getEnclosureChooserDialog(
							getString(R.string.dialog_choose_download_enclosure),
							enclosures, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									downloadEnclosure(enclosures[which].id);
								}
							});
					dialog.show();
					break;
				}
			}
			return true;

		case R.id.item_mark_listened:
			EpisodeHelper.markAsListened(getContentResolver(), mEpisodeUri);
			return true;

		case R.id.item_mark_new:
			EpisodeHelper.markAsNew(getContentResolver(), mEpisodeUri);
			return true;

		case R.id.item_delete:
			// TODO: confirmation dialog, AsyncTask
			EpisodeHelper
					.deleteDownload(
							getContentResolver(),
							(DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE),
							mEpisodeUri);
			return true;

		case R.id.item_website:
			Uri uri = episodeCursor.getUrlUri();
			if (uri != null) {
				intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			} else {
				Toast.makeText(this, R.string.message_no_website,
						Toast.LENGTH_SHORT).show();
			}
			return true;

		case R.id.item_share:
			intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, episodeCursor.getUrl());
			startActivity(Intent.createChooser(intent,
					getString(R.string.title_share)));
			return true;

		default:
			return ActivityHelper.handleGlobalMenu(this, item);

		}
	}

	private void downloadEnclosure(long... v) {
		if (v == null || v.length == 0) {
			v = new long[] { episodeCursor.getEnclosureId() };
		} else if (v.length == 1) {
			ContentValues values = new ContentValues();
			values.put(Episode.ENCLOSURE_ID, v[0]);
			getContentResolver().update(
					ContentUris.withAppendedId(
							VolksempfaengerContentProvider.EPISODE_URI,
							episodeCursor.getId()), values, null, null);
		}
		Intent intent = new Intent(this, DownloadService.class);
		intent.putExtra("id", new long[] { episodeCursor.getId() });
		startService(intent);
		// the service will send a Toast as user feedback
	}

	private static class EnclosureSimple {
		public long id;
		public String url;
	}

	private EnclosureSimple[] getEnclosures() {
		Cursor cursor;
		{
			String[] projection = { Enclosure._ID, Enclosure.URL };
			cursor = getContentResolver().query(
					VolksempfaengerContentProvider.ENCLOSURE_URI, projection,
					WHERE_EPISODE_ID,
					new String[] { String.valueOf(mEpisodeId) }, null);
		}
		EnclosureSimple[] enclosures = new EnclosureSimple[cursor.getCount()];
		int i = 0;
		while (cursor.moveToNext()) {
			EnclosureSimple enclosure = new EnclosureSimple();
			enclosure.id = cursor.getLong(cursor.getColumnIndex(Enclosure._ID));
			enclosure.url = cursor.getString(cursor
					.getColumnIndex(Enclosure.URL));
			enclosures[i] = enclosure;
			i++;
		}
		cursor.close();
		return enclosures;
	}

	private AlertDialog getEnclosureChooserDialog(String title,
			EnclosureSimple[] enclosures,
			DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		CharSequence items[] = new String[enclosures.length];
		for (int i = 0; i < enclosures.length; i++) {
			items[i] = enclosures[i].url;
		}
		builder.setItems(items, listener);
		return builder.create();
	}

	private class ImageLoadTask extends AsyncTask<Void, ImageSpan, Void> {

		private DescriptionImageDownloader imageDownloader;
		private int viewWidth;
		DisplayMetrics metrics = new DisplayMetrics();

		@Override
		protected void onPreExecute() {
			imageDownloader = new DescriptionImageDownloader(
					ViewEpisodeActivity.this);
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			viewWidth = episodeDescription.getMeasuredWidth();
		}

		@Override
		protected Void doInBackground(Void... params) {

			for (ImageSpan img : descriptionSpanned.getSpans(0,
					descriptionSpanned.length(), ImageSpan.class)) {

				if (isCancelled()) {
					return null;
				}

				if (!getImageFile(img).isFile()) {
					try {
						imageDownloader.fetchImage(img.getSource());
					} catch (Exception e) {
						// Who cares?
						Log.v(this, "Exception handled", e);
					}
				}

				if (isCancelled()) {
					return null;
				}

				publishProgress(img);
			}

			return null;

		}

		@Override
		protected void onProgressUpdate(ImageSpan... values) {
			ImageSpan img = values[0];
			File cache = getImageFile(img);
			String src = img.getSource();
			if (cache.isFile()) {
				Drawable d;
				try {
					d = new BitmapDrawable(getResources(),
							cache.getAbsolutePath());
				} catch (OutOfMemoryError e) {
					return;
				}

				int width, height;
				int originalWidthScaled = (int) (d.getIntrinsicWidth() * metrics.density);
				int originalHeightScaled = (int) (d.getIntrinsicHeight() * metrics.density);
				if (originalWidthScaled > viewWidth) {
					height = d.getIntrinsicHeight() * viewWidth
							/ d.getIntrinsicWidth();
					width = viewWidth;
				} else {
					height = originalHeightScaled;
					width = originalWidthScaled;
				}
				d.setBounds(0, 0, width, height);
				ImageSpan newImg = new ImageSpan(d, src);
				int start = descriptionSpanned.getSpanStart(img);
				int end = descriptionSpanned.getSpanEnd(img);
				if (start == -1 || end == -1) {
					return;
				}
				descriptionSpanned.removeSpan(img);
				descriptionSpanned.setSpan(newImg, start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				// explicitly update description
				episodeDescription.setText(descriptionSpanned);
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			lastImageLoadTask = null;
		}

		@Override
		protected void onCancelled(Void result) {
			lastImageLoadTask = null;
		}

		private File getImageFile(ImageSpan img) {
			return getImageFile(img.getSource());
		}

		private File getImageFile(String url) {
			return Utils.getDescriptionImageFile(ViewEpisodeActivity.this, url);
		}

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { Episode._ID, Episode.TITLE,
				Episode.DESCRIPTION, Episode.STATUS, Episode.DATE,
				Episode.DURATION_TOTAL, Episode.DURATION_LISTENED, Episode.URL,
				Episode.PODCAST_ID, Episode.PODCAST_TITLE, Episode.DOWNLOAD_ID,
				Episode.DOWNLOAD_BYTES_DOWNLOADED_SO_FAR,
				Episode.DOWNLOAD_LOCAL_URI, Episode.DOWNLOAD_STATUS,
				Episode.DOWNLOAD_TOTAL_SIZE_BYTES, Episode.ENCLOSURE_ID,
				Episode.ENCLOSURE_SIZE, Episode.FLATTR_STATUS,
				Episode.FLATTR_URL, Episode.ENCLOSURE_NUMBER };
		return new CursorLoader(this, mEpisodeUri, projection, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		episodeCursor = new EpisodeCursor(cursor);
		if (!episodeCursor.moveToFirst()) {
			// the episode does not exist (any more)
			finish();
			return;
		}
		onEpisodeCursorChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		episodeCursor = null;
		onEpisodeCursorChanged();
	}

	public void onEpisodeCursorChanged() {
		if (episodeCursor == null)
			return;

		setTitle(episodeCursor.getTitle());

		subscriptionId = episodeCursor.getPodcastId();

		invalidateOptionsMenu();

		episodeTitle.setText(episodeCursor.getTitle());

		StringBuilder meta = new StringBuilder();
		int duration = episodeCursor.getDurationTotal();
		if (duration > 0) {
			meta.append(Utils.formatTime(duration));
		} else {
			meta.append(getString(R.string.unknown_duration));
		}
		meta.append("  ");
		long size = episodeCursor.getDownloadTotal();
		if (size <= 0) {
			size = episodeCursor.getEnclosureSize();
		}
		if (size > 0) {
			meta.append(String.format("%.2f MiB", ((double) size)
					/ (1024 * 1024)));
		} else {
			meta.append(getString(R.string.unknown_size));
		}
		episodeMeta.setText(meta);
		if (episodeCursor.getFlattrStatus() != Constants.FLATTR_STATE_NONE) {
			episodeMeta.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getResources().getDrawable(R.drawable.flattr_logo), null);
		} else {
			episodeMeta.setCompoundDrawables(null, null, null, null);
		}

		if (lastImageLoadTask != null) {
			lastImageLoadTask.cancel(true);
		}

		String description = episodeCursor.getDescription();
		if (description != null) {
			if (description.hashCode() != descriptionHash) {
				descriptionHash = description.hashCode();
				Spanned s = Html.fromHtml(description, null, new TagHandler() {
					@Override
					public void handleTag(boolean opening, String tag,
							Editable output, XMLReader xmlReader) {
						if (!opening) {
							if (tag.equals("li") || tag.equals("tr")
									|| tag.equals("table") || tag.equals("ol")
									|| tag.equals("ul")) {
								output.append("\n");
							} else if (tag.equals("td")) {
								output.append(" ");
							}
						}
					}
				});
				descriptionSpanned = s instanceof SpannableStringBuilder ? (SpannableStringBuilder) s
						: new SpannableStringBuilder(s);
				if (descriptionSpanned.getSpans(0, descriptionSpanned.length(),
						CharacterStyle.class).length == 0) {
					// use the normal text as there is no html
					episodeDescription.setText(episodeCursor.getDescription());
				} else {
					episodeDescription.setText(descriptionSpanned);
					lastImageLoadTask = new ImageLoadTask()
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
		} else {
			episodeDescription.setText("");
			descriptionHash = "".hashCode();
		}

		SharedPreferences prefs = ((VolksempfaengerApplication) getApplication())
				.getSharedPreferences();
		String flattrAutoPrefs = prefs.getString(PreferenceKeys.FLATTR_AUTO,
				null);
		if (flattrAutoPrefs == null
				|| flattrAutoPrefs
						.equals(net.x4a42.volksempfaenger.Constants.PREF_AUTO_FLATTR_NEVER)) {
			switch (episodeCursor.getFlattrStatus()) {
			case Constants.FLATTR_STATE_NONE:
				flattrButton.setVisibility(View.GONE);
				break;
			case Constants.FLATTR_STATE_NEW:
				flattrButton.setVisibility(View.VISIBLE);
				flattrButton.setEnabled(true);
				flattrButton
						.setBackgroundResource(R.drawable.flattr_button_background);
				break;
			case Constants.FLATTR_STATE_PENDING:
			case Constants.FLATTR_STATE_FLATTRED:
				flattrButton.setVisibility(View.VISIBLE);
				flattrButton.setEnabled(false);
				flattrButton
						.setBackgroundResource(R.drawable.flattr_button_background_flattred);
				break;
			}
		}
	}

	public Uri getUri() {
		return mEpisodeUri;
	}

	@Override
	public void onUpPressed() {
		Intent intent;
		if (subscriptionId != -1) {
			intent = NavUtils.getParentActivityIntent(this);
			intent.putExtra("id", subscriptionId);
			TaskStackBuilder.create(this)
					.addNextIntent(new Intent(this, MainActivity.class))
					.addNextIntent(intent).startActivities();
		} else {
			intent = new Intent(this, SubscriptionGridFragment.class);
			intent.putExtra("tag", MainActivity.TAG_SUBSCRIPTIONS);
		}
		NavUtils.navigateUpTo(this, intent);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
		remote = ((PlaybackBinder) binder).getRemote();
		remote.registerEventListener(this);
		invalidateOptionsMenu();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		remote = null;

		Intent intent = new Intent(this, PlaybackService.class);
		startService(intent);
		bindService(intent, this, Activity.BIND_AUTO_CREATE);
	}

	@Override
	public void onPlaybackEvent(Event event) {
		invalidateOptionsMenu();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_flattr) {
			SharedPreferences prefs = ((VolksempfaengerApplication) getApplication())
					.getSharedPreferences();
			String username = prefs.getString(PreferenceKeys.FLATTR_USERNAME,
					null);
			if (username != null) {
				EpisodeHelper.flattr(getContentResolver(), mEpisodeId);
				Intent intent = new Intent(this, FlattrService.class);
				startService(intent);
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.title_flattr_no_account).setMessage(
						R.string.message_flattr_no_account);
				builder.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.setPositiveButton(R.string.button_flattr_auth,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										ViewEpisodeActivity.this,
										SettingsActivity.class);
								intent.putExtra(
										PreferenceActivity.EXTRA_SHOW_FRAGMENT,
										FlattrSettingsFragment.class.getName());
								startActivity(intent);
							}
						});
				builder.create().show();
			}
		}
	}
}
