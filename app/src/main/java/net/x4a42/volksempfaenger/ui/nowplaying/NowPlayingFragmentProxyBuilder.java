package net.x4a42.volksempfaenger.ui.nowplaying;

import net.x4a42.volksempfaenger.event.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventReceiverBuilder;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManagerBuilder;

class NowPlayingFragmentProxyBuilder
{
    public NowPlayingFragmentProxy build(NowPlayingFragment fragment)
    {
        PlaybackServiceConnectionManager connectionManager
                = new PlaybackServiceConnectionManagerBuilder().build(fragment.getActivity());

        PlaybackEventReceiver playbackEventReceiver
                = new PlaybackEventReceiverBuilder().build();

        SeekBarManager seekBarManager
                = new SeekBarManagerBuilder().build(fragment.getActivity(), connectionManager);

        ControlButtonsManager controlButtonsManager
                = new ControlButtonsManagerBuilder().build(fragment.getActivity(), connectionManager);

        InfoSectionManager infoSectionManager
                = new InfoSectionManagerBuilder().build(fragment.getActivity(), connectionManager);

        NowPlayingFragmentProxy proxy
                = new NowPlayingFragmentProxy(connectionManager,
                                              playbackEventReceiver,
                                              seekBarManager,
                                              controlButtonsManager,
                                              infoSectionManager);

        connectionManager.setListener(proxy);

        playbackEventReceiver.setListener(proxy);

        return proxy;
    }
}
