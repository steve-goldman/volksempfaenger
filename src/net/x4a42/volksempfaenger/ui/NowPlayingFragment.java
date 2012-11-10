package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.service.PlaybackHelper;
import net.x4a42.volksempfaenger.service.PlaybackHelper.Event;
import net.x4a42.volksempfaenger.service.PlaybackHelper.EventListener;
import net.x4a42.volksempfaenger.service.PlaybackService;
import net.x4a42.volksempfaenger.service.PlaybackService.PlaybackBinder;
import net.x4a42.volksempfaenger.service.PlaybackService.PlaybackRemote;
import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
	private LinearLayout progressDisplay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updateHandler = new Handler();
		onServiceDisconnected(null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// create ContextThemeWrapper from the original Activity Context with
		// the custom theme
		Context context = new ContextThemeWrapper(getActivity(),
				android.R.style.Theme_Holo);
		// clone the inflater using the ContextThemeWrapper
		LayoutInflater completelyDifferentInflater = inflater.cloneInContext(context);

		View view = completelyDifferentInflater.inflate(R.layout.nowplaying, container, false);

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
		progressDisplay = (LinearLayout) view
				.findViewById(R.id.progress_display);

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
			if (remote.isPlaying()) {
				remote.stop();
			} else {
				remote.play();
			}
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
		View v = getView();
		if (v != null) {
			v.setVisibility(View.GONE);
		}
	}

	private void showFragment() {
		View v = getView();
		if (v != null) {
			v.setVisibility(View.VISIBLE);
		}
	}

	private void slideIn() {
		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.animator.slide_in_up);
		getView().setAnimation(animation);
		animation.start();
	}

	private void slideOut() {
		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.animator.slide_out_down);
		getView().setAnimation(animation);
		animation.start();
	}

	private void hideExtendedControls() {
		progressDisplay.setVisibility(View.GONE);
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
		progressDisplay.setVisibility(View.VISIBLE);
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
				cursor.close();
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

			if (event != null && event.equals(PlaybackHelper.Event.PLAY)
					&& getView() != null) {
				pause.setImageResource(R.drawable.ic_media_stop);
				infoPause.setImageResource(R.drawable.ic_media_stop);
				slideIn();
			}
			showFragment();
		} else if (event != null && event.equals(PlaybackHelper.Event.PAUSE)) {
			pause.setImageResource(R.drawable.ic_media_play);
			infoPause.setImageResource(R.drawable.ic_media_play);
		} else if (event != null
				&& (event.equals(PlaybackHelper.Event.STOP) || event
						.equals(PlaybackHelper.Event.END)) && getView() != null) {
			episodeUri = null;
			slideOut();
			hideFragment();
		} else if (event == null) {
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
