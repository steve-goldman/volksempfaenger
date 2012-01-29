package net.x4a42.volksempfaenger.ui;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns.Enclosure;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.net.DescriptionImageDownloader;
import net.x4a42.volksempfaenger.service.DownloadService;
import net.x4a42.volksempfaenger.service.PlaybackService;
import net.x4a42.volksempfaenger.service.PlaybackService.OnPlayerEventListener;
import net.x4a42.volksempfaenger.service.PlaybackService.PlaybackBinder;
import net.x4a42.volksempfaenger.service.PlaybackService.PlayerEvent;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ViewEpisodeActivity extends FragmentActivity implements
		OnClickListener, OnSeekBarChangeListener, ServiceConnection,
		OnPlayerEventListener {

	private static final String TAG = "ViewEpisodeActivity";

	private static final String WHERE_EPISODE_ID = Enclosure.EPISODE_ID + "=?";

	private SeekBar seekBar;
	private TextView textDuration;
	private TextView textPosition;
	private ImageButton buttonPlay, buttonBack, buttonForward;
	private boolean bound = false;
	private PlaybackService service;
	private boolean startedPlaying = false;
	private Handler updateHandler;

	private long id;
	private EpisodeCursor cursor;
	private Bitmap podcastLogoBitmap;

	private PodcastLogoView podcastLogo;
	private TextView podcastTitle;
	private TextView podcastDescription;
	private TextView episodeTitle;
	private TextView episodeDescription;

	private View contentContainer;

	private SpannableStringBuilder descriptionSpanned;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_episode);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		podcastLogo = (PodcastLogoView) findViewById(R.id.podcast_logo);
		podcastTitle = (TextView) findViewById(R.id.podcast_title);
		podcastDescription = (TextView) findViewById(R.id.podcast_description);
		episodeTitle = (TextView) findViewById(R.id.episode_title);
		episodeDescription = (TextView) findViewById(R.id.episode_description);
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		buttonPlay = (ImageButton) findViewById(R.id.button_play);
		buttonBack = (ImageButton) findViewById(R.id.button_back);
		buttonForward = (ImageButton) findViewById(R.id.button_forward);
		textDuration = (TextView) findViewById(R.id.text_duration);
		textPosition = (TextView) findViewById(R.id.text_position);
		contentContainer = findViewById(R.id.contentContainer);

		episodeDescription.setMovementMethod(LinkMovementMethod.getInstance());

		seekBar.setEnabled(false);
		buttonBack.setEnabled(false);
		buttonForward.setEnabled(false);

		buttonPlay.setOnClickListener(this);
		buttonBack.setOnClickListener(this);
		buttonForward.setOnClickListener(this);
		seekBar.setOnSeekBarChangeListener(this);

		Intent intent = new Intent(this, PlaybackService.class);
		startService(intent);
		bindService(intent, this, BIND_AUTO_CREATE);
		updateHandler = new Handler();

		onNewIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);

		// Check if there is an ID
		Bundle extras = intent.getExtras();
		if (extras == null) {
			finish();
			return;
		}
		id = extras.getLong("id");
		if (id <= 0) {
			finish();
			return;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (cursor != null) {
			stopManagingCursor(cursor);
			cursor.close();
		}

		{
			Cursor c = managedQuery(ContentUris.withAppendedId(
					VolksempfaengerContentProvider.EPISODE_URI, id),
					new String[] { Episode._ID, Episode.TITLE,
							Episode.DESCRIPTION, Episode.STATUS, Episode.DATE,
							Episode.DURATION_TOTAL, Episode.DURATION_LISTENED,
							Episode.PODCAST_ID, Episode.PODCAST_TITLE,
							Episode.PODCAST_DESCRIPTION, Episode.DOWNLOAD_ID,
							Episode.DOWNLOAD_DONE, Episode.DOWNLOAD_FILE,
							Episode.DOWNLOAD_STATUS, Episode.DOWNLOAD_TOTAL,
							Episode.ENCLOSURE_ID }, null, null, null);
			cursor = new EpisodeCursor(c);
		}

		if (!cursor.moveToFirst()) {
			// ID does not exist
			finish();
			return;
		}

		if (service != null && service.isPlaying()
				&& service.getCurrentEpisode() == cursor.getId()) {
			setPlaying();
		}

		podcastTitle.setText(cursor.getPodcastTitle());
		podcastLogo.setPodcastId(cursor.getPodcastId());
		podcastDescription.setText(cursor.getPodcastDescription());
		episodeTitle.setText(cursor.getTitle());
		seekBar.setMax(cursor.getDurationTotal());
		seekBar.setProgress(cursor.getDurationListened());
		textPosition.setText(formatTime(cursor.getDurationListened()));
		textDuration.setText(formatTime(cursor.getDurationTotal()));
		updateEpisodeDescription();
	}

	@Override
	protected void onPause() {
		super.onPause();
		updateHandler.removeCallbacks(updateSliderTask);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (service != null) {
			unbindService(this);
		}
		if (podcastLogoBitmap != null) {
			podcastLogoBitmap.recycle();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_episode, menu);
		ActivityHelper.addGlobalMenu(this, menu);
		return true;
	}

	List<EnclosureSimple> enclosures;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ContentValues values = new ContentValues();
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			return true;

		case R.id.item_download:
			if (cursor.getEnclosureId() != 0) {
				// there is an preferred enclosure
				downloadEnclosure();
			} else {
				enclosures = getEnclosures();
				switch (enclosures.size()) {
				case 0:
					// no enclosures
					Toast.makeText(this,
							R.string.message_episode_without_enclosure,
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					// exactly one enclosure
					downloadEnclosure(enclosures.get(0).id);
					break;
				default:
					// multiple enclosures (they suck)
					AlertDialog dialog = getEnclosureChooserDialog(
							getString(R.string.dialog_choose_download_enclosure),
							enclosures, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									downloadEnclosure(enclosures.get(which).id);
								}
							});
					dialog.show();
					break;
				}
			}
			return true;

		case R.id.item_mark_listened:
			values.put(Episode.STATUS, Constants.EPISODE_STATE_LISTENED);
			getContentResolver().update(
					ContentUris.withAppendedId(
							VolksempfaengerContentProvider.EPISODE_URI, id),
					values, null, null);
			return true;

		case R.id.item_delete:
			// TODO: confirmation dialog, AsyncTask
			File f = cursor.getDownloadFile();
			if (f != null && f.isFile()) {
				f.delete();
			}
			values.put(Episode.DOWNLOAD_ID, 0);
			values.put(Episode.STATUS, Constants.EPISODE_STATE_LISTENED);
			getContentResolver().update(
					ContentUris.withAppendedId(
							VolksempfaengerContentProvider.EPISODE_URI,
							cursor.getId()), values, null, null);
			// TODO remove from DownloadManager
			return true;

		default:
			return ActivityHelper.handleGlobalMenu(this, item);

		}
	}

	private void downloadEnclosure(long... v) {
		if (v == null || v.length == 0) {
			v = new long[] { cursor.getEnclosureId() };
		} else if (v.length == 1) {
			ContentValues values = new ContentValues();
			values.put(Episode.ENCLOSURE_ID, v[0]);
			getContentResolver().update(
					ContentUris.withAppendedId(
							VolksempfaengerContentProvider.EPISODE_URI,
							cursor.getId()), values, null, null);
		}
		Intent intent = new Intent(this, DownloadService.class);
		intent.putExtra("id", new long[] { cursor.getId() });
		startService(intent);
		// the service will send a Toast as user feedback
	}

	private Runnable updateSliderTask = new Runnable() {
		public void run() {
			seekBar.setProgress(service.getCurrentPosition());
			updateHandler.postDelayed(this, 500);
			updateTime();
		}
	};

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_play:
			if (bound) {
				if (startedPlaying) {
					togglePlayPause();
				} else {
					if (cursor.getStatus() < Constants.EPISODE_STATE_READY
							|| cursor.getDownloadFile() == null
							|| !cursor.getDownloadFile().isFile()) {
						// enclosure file does not exist
						// TODO: auto download, streaming?
						Log.d(TAG, "getEpisodeStatus(): " + cursor.getStatus());
						Log.d(TAG,
								"getEnclosureFile(): "
										+ cursor.getDownloadFile());
						Toast.makeText(this,
								R.string.message_enclosure_file_not_available,
								Toast.LENGTH_SHORT).show();
					} else {
						try {
							service.playEpisode(ContentUris.withAppendedId(
									VolksempfaengerContentProvider.EPISODE_URI,
									cursor.getId()));
							startedPlaying = true;
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			break;
		case R.id.button_back:
			if (bound) {
				int newPosition = service.getCurrentPosition() - 30000;
				if (newPosition < 0) {
					newPosition = 0;
				}
				service.seekTo(newPosition);
			}
			break;
		case R.id.button_forward:
			if (bound) {
				int newPosition = service.getCurrentPosition() + 30000;
				int duration = service.getDuration();
				if (newPosition > duration) {
					newPosition = duration - 1000;
				}
				service.seekTo(newPosition);
			}
			break;
		}
	}

	private void togglePlayPause() {
		if (service.isPlaying()) {
			setButtonPlay();
			service.pause();
		} else {
			setButtonPause();
			buttonPlay.setImageResource(R.drawable.ic_media_pause);
			service.play();
		}
	}

	private void setPlaying() {
		startedPlaying = true;
		setButtonPause();
		textDuration.setText(formatTime(service.getDuration()));
		seekBar.setMax(service.getDuration());
		seekBar.setEnabled(true);
		buttonBack.setEnabled(true);
		buttonForward.setEnabled(true);
		updateHandler.removeCallbacks(updateSliderTask);
		updateHandler.post(updateSliderTask);
	}

	private void setButtonPlay() {
		buttonPlay.setImageResource(R.drawable.ic_media_play);
	}

	private void setButtonPause() {
		buttonPlay.setImageResource(R.drawable.ic_media_pause);
	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser && startedPlaying) {
			updateHandler.removeCallbacks(updateSliderTask);
			service.seekTo(progress);
			updateTime();
			updateHandler.post(updateSliderTask);
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		updateHandler.removeCallbacks(updateSliderTask);
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	private void updateTime() {
		textPosition.setText(formatTime(service.getCurrentPosition()));
	}

	private String formatTime(int milliseconds) {
		int seconds = milliseconds / 1000;
		int hours = seconds / 3600;
		int minutes = (seconds / 60) - (hours * 60);
		int seconds2 = seconds - (minutes * 60) - (hours * 3600);
		DecimalFormat format = new DecimalFormat("00");
		return format.format(hours) + ":" + format.format(minutes) + ":"
				+ format.format(seconds2);
	}

	public void onServiceConnected(ComponentName name, IBinder binder) {
		service = ((PlaybackBinder) binder).getService();
		service.addOnPlayerEventListener(this);
		if (service.isPlaying()
				&& service.getCurrentEpisode() == cursor.getId()) {
			setPlaying();
		}
		bound = true;
	}

	public void onServiceDisconnected(ComponentName name) {
		Log.e(TAG, "Service disconnected");
		bound = false;
	}

	public void onPlayerPrepared() {
	}

	private class EnclosureSimple {
		public long id;
		public String url;
	}

	private List<EnclosureSimple> getEnclosures() {
		Cursor cursor;
		{
			String[] projection = { Enclosure._ID, Enclosure.URL };
			cursor = getContentResolver()
					.query(VolksempfaengerContentProvider.ENCLOSURE_URI,
							projection, WHERE_EPISODE_ID,
							new String[] { String.valueOf(id) }, null);
		}
		List<EnclosureSimple> enclosures = new ArrayList<EnclosureSimple>();
		while (cursor.moveToNext()) {
			EnclosureSimple enclosure = new EnclosureSimple();
			enclosure.id = cursor.getLong(cursor.getColumnIndex(Enclosure._ID));
			enclosure.url = cursor.getString(cursor
					.getColumnIndex(Enclosure.URL));
			enclosures.add(enclosure);
		}
		cursor.close();
		return enclosures;
	}

	private AlertDialog getEnclosureChooserDialog(String title,
			List<EnclosureSimple> enclosures,
			DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		CharSequence items[] = new String[enclosures.size()];
		for (int i = 0; i < enclosures.size(); i++) {
			items[i] = enclosures.get(i).url;
		}
		builder.setItems(items, listener);
		return builder.create();
	}

	private void updateEpisodeDescription() {
		Spanned s = Html.fromHtml(cursor.getDescription());
		descriptionSpanned = s instanceof SpannableStringBuilder ? (SpannableStringBuilder) s
				: new SpannableStringBuilder(s);
		if (descriptionSpanned.getSpans(0, descriptionSpanned.length(),
				CharacterStyle.class).length == 0) {
			// use the normal text as there is no html
			episodeDescription.setText(cursor.getDescription());
		} else {
			episodeDescription.setText(descriptionSpanned);
			new ImageLoadTask().execute();
		}
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
			viewWidth = contentContainer.getMeasuredWidth();
		}

		@Override
		protected Void doInBackground(Void... params) {

			for (ImageSpan img : descriptionSpanned.getSpans(0,
					descriptionSpanned.length(), ImageSpan.class)) {
				if (!getImageFile(img).isFile()) {
					try {
						imageDownloader.fetchImage(img.getSource());
					} catch (Exception e) {
						// Who cares?
						Log.d(getClass().getSimpleName(), "Exception handled",
								e);
					}
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
				Drawable d = new BitmapDrawable(getResources(),
						cache.getAbsolutePath());

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
				descriptionSpanned.removeSpan(img);
				descriptionSpanned.setSpan(newImg, start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				// explicitly update description
				episodeDescription.setText(descriptionSpanned);
			}
		}

		private File getImageFile(ImageSpan img) {
			return getImageFile(img.getSource());
		}

		private File getImageFile(String url) {
			return Utils.getDescriptionImageFile(ViewEpisodeActivity.this, url);
		}

	}

	@Override
	public void onPlayerEvent(PlayerEvent event) {
		switch (event) {
		case PAUSE:
			setButtonPlay();
			break;
		case PREPARE:
			setPlaying();
			break;
		case STOP:
			// TODO clean up
			setButtonPlay();
			seekBar.setEnabled(false);
			buttonBack.setEnabled(false);
			buttonForward.setEnabled(false);
			textPosition.setText("00:00:00");
			textDuration.setText("00:00:00");
			startedPlaying = false;
			break;
		}
	}

}
