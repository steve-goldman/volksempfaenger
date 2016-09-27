package net.x4a42.volksempfaenger.service.playback;

import android.content.Intent;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;

class IntentParser
{
    public interface Listener
    {
        void onPlay(Episode episode);
        void onPause();
        void onPlayPause();
        void onStop();
        void onSeek(int position);
        void onMove(int offset);
    }

    private final EpisodeDaoWrapper episodeDao;
    private Listener listener;

    public IntentParser(EpisodeDaoWrapper episodeDao)
    {
        this.episodeDao = episodeDao;
    }

    public IntentParser setListener(Listener listener)
    {
        this.listener = listener;
        return this;
    }

    public void parse(Intent intent)
    {
        if (listener == null)
        {
            return;
        }

        if (intent == null)
        {
            return;
        }

        String action = intent.getAction();
        if (action == null)
        {
            return;
        }

        switch (action)
        {
            case PlaybackService.ActionPlay:
                handlePlay(intent);
                break;
            case PlaybackService.ActionPause:
                handlePause();
                break;
            case PlaybackService.ActionPlayPause:
                handlePlayPause();
                break;
            case PlaybackService.ActionStop:
                handleStop();
                break;
            case PlaybackService.ActionSeek:
                handleSeek(intent);
                break;
            case PlaybackService.ActionMove:
                handleMove(intent);
                break;
            default:
                Log.e(this, String.format("unexpected action:%s", action));
                break;
        }
    }

    private void handlePlay(Intent intent)
    {
        long    episodeId = intent.getLongExtra(PlaybackServiceIntentProvider.EpisodeIdKey, -1);
        Episode episode   = episodeDao.getById(episodeId);
        listener.onPlay(episode);
    }

    private void handlePause()
    {
        listener.onPause();
    }

    private void handlePlayPause()
    {
        listener.onPlayPause();
    }

    private void handleStop()
    {
        listener.onStop();
    }

    private void handleSeek(Intent intent)
    {
        int position = intent.getIntExtra(PlaybackServiceIntentProvider.PositionKey, 0);
        listener.onSeek(position);
    }

    private void handleMove(Intent intent)
    {
        int offset = intent.getIntExtra(PlaybackServiceIntentProvider.OffsetKey, 0);
        listener.onMove(offset);
    }
}
