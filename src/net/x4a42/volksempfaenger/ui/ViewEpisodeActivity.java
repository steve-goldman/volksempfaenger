package net.x4a42.volksempfaenger.ui;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.DatabaseHelper;
import net.x4a42.volksempfaenger.net.DescriptionImageDownloader;
import net.x4a42.volksempfaenger.service.DownloadService;
import net.x4a42.volksempfaenger.service.PlaybackService;
import net.x4a42.volksempfaenger.service.PlaybackService.PlaybackBinder;
import net.x4a42.volksempfaenger.service.PlaybackService.PlayerListener;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ViewEpisodeActivity extends BaseActivity implements
		OnClickListener, OnSeekBarChangeListener, ServiceConnection,
		PlayerListener {
	private SeekBar seekBar;
	private TextView textDuration;
	private TextView textPosition;
	private ImageButton buttonPlay, buttonBack, buttonForward;
	private boolean bound = false;
	private PlaybackService service;
	private boolean startedPlaying = false;
	private Handler updateHandler;

	private long id;
	private DatabaseHelper dbHelper;
	private Cursor cursor;

	private ImageView podcastLogo;
	private TextView podcastTitle;
	private TextView podcastDescription;
	private TextView episodeTitle;
	private TextView episodeDescription;

	private SpannableStringBuilder descriptionSpanned;

	private long getPodcastId() {
		return cursor.getLong(cursor
				.getColumnIndex(DatabaseHelper.ExtendedEpisode.PODCAST_ID));
	}

	private String getPodcastDescription() {
		return cursor
				.getString(cursor
						.getColumnIndex(DatabaseHelper.ExtendedEpisode.PODCAST_DESCRIPTION));
	}

	private Bitmap getPodcastLogoBitmap() {
		File podcastLogoFile = Utils.getPodcastLogoFile(this, getPodcastId());
		if (podcastLogoFile.isFile()) {
			return BitmapFactory.decodeFile(podcastLogoFile.getAbsolutePath());
		} else {
			return BitmapFactory.decodeResource(getResources(),
					R.drawable.default_logo);
		}
	}

	private long getEnclosureId() {
		return cursor.getLong(cursor
				.getColumnIndex(DatabaseHelper.ExtendedEpisode.ENCLOSURE_ID));
	}

	private String getEnclosureFile() {
		return cursor.getString(cursor
				.getColumnIndex(DatabaseHelper.ExtendedEpisode.ENCLOSURE_FILE));
	}

	private String getEpisodeTitle() {
		return cursor.getString(cursor
				.getColumnIndex(DatabaseHelper.ExtendedEpisode.EPISODE_TITLE));
	}

	private String getEpisodeDescription() {
		return cursor
				.getString(cursor
						.getColumnIndex(DatabaseHelper.ExtendedEpisode.EPISODE_DESCRIPTION));
	}

	private String getPodcastTitle() {
		return cursor.getString(cursor
				.getColumnIndex(DatabaseHelper.ExtendedEpisode.PODCAST_TITLE));
	}

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

		dbHelper = DatabaseHelper.getInstance(this);

		cursor = dbHelper.getReadableDatabase().query(
				DatabaseHelper.ExtendedEpisode._TABLE, null,
				String.format("%s = ?", DatabaseHelper.ExtendedEpisode.ID),
				new String[] { String.valueOf(id) }, null, null, null);
		startManagingCursor(cursor);

		podcastLogo = (ImageView) findViewById(R.id.podcast_logo);
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
	}

	@Override
	public void onPause() {
		super.onResume();
		updateHandler.removeCallbacks(updateSliderTask);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (service != null && service.isPlaying()) {
			updateHandler.post(updateSliderTask);
		}

		cursor.requery();
		if (!cursor.moveToFirst()) {
			// ID does not exist
			finish();
			return;
		}

		podcastTitle.setText(getPodcastTitle());
		podcastLogo.setImageBitmap(getPodcastLogoBitmap());
		podcastDescription.setText(getPodcastDescription());
		episodeTitle.setText(getEpisodeTitle());
		updateEpisodeDescription();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (service != null) {
			unbindService(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_episode, menu);
		addGlobalMenu(menu);
		return true;
	}

	List<EnclosureSimple> enclosures;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ContentValues values = new ContentValues();
		switch (item.getItemId()) {
		case R.id.item_download:
			enclosures = getEnclosures();
			switch (enclosures.size()) {
			case 0:
				Toast.makeText(this,
						R.string.message_episode_without_enclosure,
						Toast.LENGTH_SHORT).show();
				return true;
			case 1:
				long v[] = new long[1];
				v[0] = enclosures.get(0).id;
				downloadEnclosure(v);
				break;
			default:
				AlertDialog dialog = getEnclosureChooserDialog(
						getString(R.string.dialog_choose_download_enclosure),
						enclosures, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								long v[] = new long[1];
								v[0] = enclosures.get(which).id;
								downloadEnclosure(v);
							}
						});
				dialog.show();
				break;
			}
			return true;

		case R.id.item_mark_listened:
			values.put(DatabaseHelper.Episode.STATE,
					DatabaseHelper.Episode.STATE_LISTENED);
			dbHelper.getWritableDatabase().update(
					DatabaseHelper.Episode._TABLE, values,
					String.format("%s = ?", DatabaseHelper.Episode.ID),
					new String[] { String.valueOf(id) });
			return true;

		case R.id.item_delete:
			// TODO: confirmation dialog, AsyncTask
			try {
				if (getEnclosureFile() != null) {
					File f = new File(new URI(getEnclosureFile()));
					if (f.isFile()) {
						f.delete();
					}
				}
			} catch (URISyntaxException e) {
				Log.w(getClass().getSimpleName(), "Exception handled", e);
			}
			values.put(DatabaseHelper.Enclosure.FILE, (String) null);
			dbHelper.getReadableDatabase().update(
					DatabaseHelper.Enclosure._TABLE, values,
					String.format("%s = ?", DatabaseHelper.Enclosure.ID),
					new String[] { String.valueOf(getEnclosureId()) });
			values.clear();
			values.put(DatabaseHelper.Episode.STATE,
					DatabaseHelper.Episode.STATE_LISTENED);
			dbHelper.getReadableDatabase().update(
					DatabaseHelper.Episode._TABLE, values,
					String.format("%s = ?", DatabaseHelper.Episode.ID),
					new String[] { String.valueOf(id) });
			return true;

		default:
			return handleGlobalMenu(item);
		}
	}

	private void downloadEnclosure(long[] v) {
		Intent intent = new Intent(this, DownloadService.class);
		intent.putExtra("id", v);
		startService(intent);
		Toast.makeText(this, R.string.message_download_queued,
				Toast.LENGTH_SHORT).show();
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
					try {
						// TODO change to actual file name
						service.playFile("/mnt/sdcard/test.mp3");
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
			buttonPlay.setImageResource(android.R.drawable.ic_media_pause);
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
		buttonPlay.setImageResource(android.R.drawable.ic_media_play);
	}

	private void setButtonPause() {
		buttonPlay.setImageResource(android.R.drawable.ic_media_pause);
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
		service.setPlayerListener(this);
		if (service.isPlaying()) {
			setPlaying();
		}
		bound = true;
	}

	public void onServiceDisconnected(ComponentName name) {
		Log.e(TAG, "Service disconnected");
		bound = false;
	}

	public void onPlayerPaused() {
		setButtonPlay();
	}

	public void onPlayerStopped() {
		// TODO clean up
		setButtonPlay();
		seekBar.setEnabled(false);
		buttonBack.setEnabled(false);
		buttonForward.setEnabled(false);

		textPosition.setText("00:00:00");
		textDuration.setText("00:00:00");
	}

	public void onPlayerPrepared() {
		service.play();
		setPlaying();
	}

	private class EnclosureSimple {
		public long id;
		public String url;
	}

	private List<EnclosureSimple> getEnclosures() {
		Cursor cursor = dbHelper.getReadableDatabase().query(
				DatabaseHelper.Enclosure._TABLE,
				new String[] { DatabaseHelper.Enclosure.ID,
						DatabaseHelper.Enclosure.URL },
				String.format("%s = ?", DatabaseHelper.Enclosure.EPISODE),
				new String[] { String.valueOf(id) }, null, null, null);
		List<EnclosureSimple> enclosures = new ArrayList<EnclosureSimple>();
		while (cursor.moveToNext()) {
			EnclosureSimple enclosure = new EnclosureSimple();
			enclosure.id = cursor.getLong(cursor
					.getColumnIndex(DatabaseHelper.Enclosure.ID));
			enclosure.url = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.Enclosure.URL));
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
		Spanned s = Html.fromHtml(getEpisodeDescription());
		descriptionSpanned = s instanceof SpannableStringBuilder ? (SpannableStringBuilder) s
				: new SpannableStringBuilder(s);
		if (descriptionSpanned.getSpans(0, descriptionSpanned.length(),
				CharacterStyle.class).length == 0) {
			// use the normal text as there is no html
			episodeDescription.setText(getEpisodeDescription());
		} else {
			episodeDescription.setText(descriptionSpanned);
			new ImageLoadTask().execute();
		}
	}

	private class ImageLoadTask extends AsyncTask<Void, ImageSpan, Void> {
		private DescriptionImageDownloader imageDownloader;
		DisplayMetrics metrics = new DisplayMetrics();

		@Override
		protected void onPreExecute() {
			imageDownloader = new DescriptionImageDownloader(
					ViewEpisodeActivity.this);
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
				if (originalWidthScaled > metrics.widthPixels) {
					height = d.getIntrinsicHeight() * metrics.widthPixels
							/ d.getIntrinsicWidth();
					width = metrics.widthPixels;
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

}
