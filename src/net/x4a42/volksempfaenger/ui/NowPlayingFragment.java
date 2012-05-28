package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.service.PlaybackHelper.Event;
import net.x4a42.volksempfaenger.service.PlaybackHelper.EventListener;
import net.x4a42.volksempfaenger.service.PlaybackService;
import net.x4a42.volksempfaenger.service.PlaybackService.PlaybackBinder;
import net.x4a42.volksempfaenger.service.PlaybackService.PlaybackRemote;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class NowPlayingFragment extends Fragment implements ServiceConnection,
		OnClickListener, OnSeekBarChangeListener, EventListener {

	private Uri episodeUri;
	private boolean isPlaying;

	private LinearLayout info;
	private PodcastLogoView logo;
	private LinearLayout episodeInfo;
	private TextView episode;
	private TextView podcast;
	private ImageButton infoPause;
	private SeekBar seekbar;
	private LinearLayout controls;
	private TextView position;
	private ImageButton back;
	private ImageButton pause;
	private ImageButton forward;
	private TextView duration;

	private PlaybackRemote remote;
	private Handler updateHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updateHandler = new Handler();
		onServiceDisconnected(null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.nowplaying, container, false);

		info = (LinearLayout) view.findViewById(R.id.info);
		logo = (PodcastLogoView) view.findViewById(R.id.logo);
		episodeInfo = (LinearLayout) view.findViewById(R.id.episode_info);
		episode = (TextView) view.findViewById(R.id.episode);
		podcast = (TextView) view.findViewById(R.id.podcast);
		infoPause = (ImageButton) view.findViewById(R.id.info_pause);
		seekbar = (SeekBar) view.findViewById(R.id.seekbar);
		controls = (LinearLayout) view.findViewById(R.id.controls);
		position = (TextView) view.findViewById(R.id.position);
		back = (ImageButton) view.findViewById(R.id.back);
		pause = (ImageButton) view.findViewById(R.id.pause);
		forward = (ImageButton) view.findViewById(R.id.forward);
		duration = (TextView) view.findViewById(R.id.duration);

		episodeInfo.setOnClickListener(this);
		infoPause.setOnClickListener(this);
		seekbar.setEnabled(true);
		seekbar.setOnSeekBarChangeListener(this);
		back.setOnClickListener(this);
		pause.setOnClickListener(this);
		forward.setOnClickListener(this);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (remote == null) {
			onServiceDisconnected(null);
		} else {
			startUpdater();
			remote.registerEventListener(this);
			onPlaybackEvent(null);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		stopUpdater();

		if (remote != null) {
			remote.unregisterEventListener(this);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (remote != null) {
			getActivity().unbindService(this);
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
		System.out.println(name);
		remote = ((PlaybackBinder) binder).getRemote();
		remote.registerEventListener(this);
		onPlaybackEvent(null);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		remote = null;

		Intent intent = new Intent(getActivity(), PlaybackService.class);
		getActivity().startService(intent);
		getActivity().bindService(intent, this, Activity.BIND_AUTO_CREATE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.episode_info:
			onClickInfo(v);
			break;
		case R.id.info_pause:
		case R.id.pause:
			onClickPause(v);
			break;
		case R.id.back:
			onClickBack(v);
			break;
		case R.id.forward:
			onClickForward(v);
			break;
		}
	}

	public void onClickInfo(View v) {
		Intent intent = new Intent(getActivity(), ViewEpisodeActivity.class);
		if (remote != null) {
			intent.setData(remote.getEpisodeUri());
		}
		startActivity(intent);
	}

	public void onClickPause(View v) {
		if (remote != null) {
			remote.pause();
		}
	}

	public void onClickBack(View v) {
		if (remote != null) {
			remote.movePosition(-30000);
		}
	}

	public void onClickForward(View v) {
		if (remote != null) {
			remote.movePosition(30000);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser && remote != null) {
			stopUpdater();
			remote.seekTo(progress);
			updateTime();
			startUpdater();
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		stopUpdater();
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	private void hideFragment() {
		getView().setVisibility(View.GONE);
	}

	private void showFragment() {
		getView().setVisibility(View.VISIBLE);
	}

	private void hideExtendedControls() {
		seekbar.setVisibility(View.GONE);
		controls.setVisibility(View.GONE);
		info.setVisibility(View.VISIBLE);
	}

	private void showExtendedControls(EpisodeCursor cursor) {
		startUpdater();
		seekbar.setMax(cursor.getDurationTotal());
		seekbar.setProgress(cursor.getDurationListened());
		seekbar.setMax(remote.getDuration());
		position.setText(Utils.formatTime(cursor.getDurationListened()));
		duration.setText(Utils.formatTime(cursor.getDurationTotal()));
		info.setVisibility(View.GONE);
		seekbar.setVisibility(View.VISIBLE);
		controls.setVisibility(View.VISIBLE);
	}

	@Override
	public void onPlaybackEvent(Event event) {
		if (remote.isPlaying() == isPlaying && isPlaying
				&& remote.getEpisodeUri().equals(episodeUri)) {
			// nothing changed
			return;
		}

		isPlaying = remote.isPlaying();
		episodeUri = remote.getEpisodeUri();

		if (isPlaying) {
			EpisodeCursor cursor;
			String[] projection = { Episode.TITLE, Episode.PODCAST_ID,
					Episode.PODCAST_TITLE, Episode.DURATION_LISTENED,
					Episode.DURATION_TOTAL };
			cursor = new EpisodeCursor(getActivity().getContentResolver()
					.query(episodeUri, projection, null, null, null));

			if (!cursor.moveToFirst()) {
				throw new IllegalArgumentException("Episode not found");
			}

			episode.setText(cursor.getTitle());
			podcast.setText(cursor.getPodcastTitle());
			logo.setPodcastId(cursor.getPodcastId());

			if (getActivity() instanceof ViewEpisodeActivity) {
				ViewEpisodeActivity activity = (ViewEpisodeActivity) getActivity();
				Uri activityUri = activity.getUri();
				Uri remoteUri = remote.getEpisodeUri();
				if (activityUri != null && remoteUri != null
						&& remoteUri.equals(activityUri)) {
					showExtendedControls(cursor);
				} else {
					hideExtendedControls();
				}
			} else {
				hideExtendedControls();
			}
			cursor.close();

			showFragment();
		} else {
			episodeUri = null;
			hideFragment();
		}
	}

	private Runnable updateSliderTask = new Runnable() {
		public void run() {
			seekbar.setProgress(remote.getPosition());
			updateHandler.postDelayed(this, 500);
			updateTime();
		}
	};

	private void startUpdater() {
		stopUpdater();
		updateHandler.post(updateSliderTask);
	}

	private void stopUpdater() {
		updateHandler.removeCallbacks(updateSliderTask);
	}

	private void updateTime() {
		position.setText(Utils.formatTime(remote.getPosition()));
	}

}
