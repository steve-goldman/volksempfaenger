package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.service.PlaybackService;
import net.x4a42.volksempfaenger.service.PlaybackService.OnPlayerEventListener;
import net.x4a42.volksempfaenger.service.PlaybackService.PlaybackBinder;
import net.x4a42.volksempfaenger.service.PlaybackService.PlayerEvent;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NowPlayingFragment extends Fragment implements ServiceConnection,
		OnPlayerEventListener, OnClickListener {

	private boolean isPlaying;
	private long episodeId = -1;
	private ImageView logo;
	private LinearLayout info;
	private TextView episode;
	private TextView podcast;
	private ImageView playpause;
	private PlaybackService service;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onServiceDisconnected(null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.nowplaying, container, false);

		logo = (ImageView) view.findViewById(R.id.logo);
		info = (LinearLayout) view.findViewById(R.id.info);
		episode = (TextView) view.findViewById(R.id.episode);
		podcast = (TextView) view.findViewById(R.id.podcast);
		playpause = (ImageView) view.findViewById(R.id.playpause);

		info.setClickable(true);
		info.setOnClickListener(this);
		playpause.setClickable(true);
		playpause.setOnClickListener(this);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (service == null) {
			onServiceDisconnected(null);
		} else {
			service.addOnPlayerEventListener(this);
			onPlayerEvent(null);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if (service != null) {
			service.removeOnPlayerEventListener(this);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (service != null) {
			getActivity().unbindService(this);
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
		System.out.println(name);
		service = ((PlaybackBinder) binder).getService();
		service.addOnPlayerEventListener(this);
		onPlayerEvent(null);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		service = null;

		Intent intent = new Intent(getActivity(), PlaybackService.class);
		getActivity().startService(intent);
		getActivity().bindService(intent, this, Activity.BIND_AUTO_CREATE);
	}

	@Override
	public void onPlayerEvent(PlayerEvent event) {
		if (service.isPlaying() == isPlaying && isPlaying
				&& service.getCurrentEpisode() == episodeId) {
			// nothing changed
			return;
		}

		isPlaying = service.isPlaying();

		if (service.isPlaying()) {
			episodeId = service.getCurrentEpisode();

			Cursor cursor;
			{
				String[] projection = { Episode.TITLE, Episode.PODCAST_ID,
						Episode.PODCAST_TITLE };
				cursor = getActivity().managedQuery(
						ContentUris.withAppendedId(
								VolksempfaengerContentProvider.EPISODE_URI,
								episodeId), projection, null, null, null);
			}

			if (!cursor.moveToFirst()) {
				throw new IllegalArgumentException("Episode not found");
			}

			episode.setText(cursor.getString(cursor
					.getColumnIndex(Episode.TITLE)));
			podcast.setText(cursor.getString(cursor
					.getColumnIndex(Episode.PODCAST_TITLE)));
			logo.setImageBitmap(Utils.getPodcastLogoBitmap(getActivity(),
					cursor.getLong(cursor.getColumnIndex(Episode.PODCAST_ID))));

			getView().setVisibility(View.VISIBLE);
		} else {
			episodeId = -1;
			getView().setVisibility(View.GONE);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.info:
			if (episodeId != -1) {
				Intent intent = new Intent(getActivity(),
						ViewEpisodeActivity.class);
				intent.putExtra("id", episodeId);
				startActivity(intent);
			}
			break;
		case R.id.playpause:
			if (service != null) {
				service.pause();
			}
			break;
		}
	}
}
