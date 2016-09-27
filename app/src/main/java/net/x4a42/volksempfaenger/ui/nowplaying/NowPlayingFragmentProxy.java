package net.x4a42.volksempfaenger.ui.nowplaying;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.service.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventListener;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;

class NowPlayingFragmentProxy implements PlaybackServiceConnectionManager.Listener,
                                         PlaybackEventListener
{
    private final PlaybackServiceConnectionManager connectionManager;
    private final PlaybackEventReceiver            playbackEventReceiver;
    private final SeekBarManager                   seekBarManager;
    private final ControlButtonsManager            controlButtonsManager;
    private final InfoSectionManager               infoSectionManager;
    private Episode                                episode;

    public NowPlayingFragmentProxy(PlaybackServiceConnectionManager connectionManager,
                                   PlaybackEventReceiver            playbackEventReceiver,
                                   SeekBarManager                   seekBarManager,
                                   ControlButtonsManager            controlButtonsManager,
                                   InfoSectionManager               infoSectionManager)
    {
        this.connectionManager     = connectionManager;
        this.playbackEventReceiver = playbackEventReceiver;
        this.seekBarManager        = seekBarManager;
        this.controlButtonsManager = controlButtonsManager;
        this.infoSectionManager    = infoSectionManager;
    }

    public void setEpisode(Episode episode)
    {
        this.episode = episode;
    }

    public void onCreate()
    {
        connectionManager.onCreate();
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container)
    {
        View view = inflater.inflate(R.layout.nowplaying, container, false);

        seekBarManager.onCreateView(view);
        controlButtonsManager.onCreateView(view);
        infoSectionManager.onCreateView(view);

        return view;
    }

    public void onStart()
    {
        updateSections();
    }

    public void onResume()
    {
        playbackEventReceiver.subscribe();
        seekBarManager.onResume();
        controlButtonsManager.onResume();
        infoSectionManager.onResume();
    }

    public void onPause()
    {
        playbackEventReceiver.unsubscribe();
        seekBarManager.onPause();
        controlButtonsManager.onPause();
        infoSectionManager.onPause();
    }

    public void onDestroy()
    {
        connectionManager.onDestroy();
    }

    @Override
    public void onPlaybackServiceConnected()
    {
        controlButtonsManager.onPlaybackServiceConnected();
        infoSectionManager.onPlaybackServiceConnected();
        updateSections();

    }

    @Override
    public void onPlaybackServiceDisconnected()
    {
        updateSections();
        controlButtonsManager.onPlaybackServiceDisconnected();
        infoSectionManager.onPlaybackServiceDisconnected();
    }

    @Override
    public void onPlaybackEvent(PlaybackEvent playbackEvent)
    {
        updateSections();
    }

    private void updateSections()
    {
        PlaybackServiceFacade facade = connectionManager.getFacade();

        if (facade == null || !facade.isOpen())
        {
            seekBarManager.hide();
            controlButtonsManager.hide();
            infoSectionManager.hide();
            return;
        }

        if (episode != null && facade.isEpisodeOpen(episode))
        {
            seekBarManager.show();
            controlButtonsManager.show();
            infoSectionManager.hide();
        }
        else
        {
            seekBarManager.hide();
            controlButtonsManager.hide();
            infoSectionManager.show();
        }
    }

}
