package net.x4a42.volksempfaenger.ui;

import java.io.IOException;

import net.x4a42.volksempfaenger.R;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PlayerActivity extends BaseActivity implements OnClickListener,
		OnSeekBarChangeListener {
	private MediaPlayer mp;
	private SeekBar seekBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);

		seekBar = (SeekBar) findViewById(R.id.seekBar1);

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
				seekBar.setMax(mp.getDuration());
				updateHandler.post(updateSliderTask);
			}
		});
		mp.prepareAsync();
	}

	private Handler updateHandler;

	private Runnable updateSliderTask = new Runnable() {
		public void run() {
			seekBar.setProgress(mp.getCurrentPosition());
			updateHandler.postDelayed(this, 500);
		}
	};

	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			mp.seekTo(progress);
		}

	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		updateHandler.removeCallbacks(updateSliderTask);
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		updateHandler.postDelayed(updateSliderTask, 500);
	}
}
