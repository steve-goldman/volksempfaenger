package net.x4a42.volksempfaenger.ui;

import java.io.IOException;
import java.text.DecimalFormat;

import net.x4a42.volksempfaenger.R;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PlayerActivity extends BaseActivity implements OnClickListener,
		OnSeekBarChangeListener {
	private MediaPlayer mp;
	private SeekBar seekBar;
	private TextView textTime;
	private String durationString;
	private ImageButton buttonPlay;
	boolean isPlaying = false;
	boolean prepared = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);

		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		buttonPlay = (ImageButton) findViewById(R.id.button_play);
		textTime = (TextView) findViewById(R.id.text_time);

		buttonPlay.setOnClickListener(this);
	}
	
	@Override
	public void onPause() {
		super.onResume();
		if(updateHandler != null) {
			updateHandler.removeCallbacks(updateSliderTask);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(updateHandler != null) {
			updateHandler.post(updateSliderTask);
		}
	}

	private Handler updateHandler;

	private Runnable updateSliderTask = new Runnable() {
		public void run() {
			seekBar.setProgress(mp.getCurrentPosition());
			updateHandler.postDelayed(this, 500);
			updateTime();
		}
	};

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_play:
			if (prepared) {
				togglePlayPause();
			} else {
				prepared = true;
				mp = new MediaPlayer();
				try {
					mp.setDataSource("/mnt/sdcard/test.mp3");
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				updateHandler = new Handler();
				seekBar.setOnSeekBarChangeListener(this);
				mp.setOnPreparedListener(new OnPreparedListener() {

					public void onPrepared(MediaPlayer mp) {
						mp.start();
						togglePlayPause();
						durationString = formatTime(mp.getDuration());
						seekBar.setMax(mp.getDuration());
						updateHandler.post(updateSliderTask);
					}
				});
				mp.prepareAsync();
			}
		}
	}

	private void togglePlayPause() {
		if (isPlaying) {
			buttonPlay.setImageResource(android.R.drawable.ic_media_play);
			mp.pause();
		} else {
			buttonPlay.setImageResource(android.R.drawable.ic_media_pause);
			mp.start();
		}
		buttonPlay.invalidate();
		isPlaying = !isPlaying;
	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			updateHandler.removeCallbacks(updateSliderTask);
			mp.seekTo(progress);
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
		textTime.setText(formatTime(mp.getCurrentPosition()) + "/"
				+ durationString);
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
}
