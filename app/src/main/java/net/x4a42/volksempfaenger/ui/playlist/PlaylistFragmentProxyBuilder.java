package net.x4a42.volksempfaenger.ui.playlist;

import net.x4a42.volksempfaenger.event.episodedownload.EpisodeDownloadEventReceiver;
import net.x4a42.volksempfaenger.event.episodedownload.EpisodeDownloadEventReceiverBuilder;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventReceiverBuilder;

class PlaylistFragmentProxyBuilder
{
    public PlaylistFragmentProxy build(PlaylistFragment fragment)
    {
        ListManager listManager = new ListManagerBuilder().build(fragment.getActivity());

        PlaybackEventReceiver playbackEventReceiver
                = new PlaybackEventReceiverBuilder().build().setListener(listManager);

        EpisodeDownloadEventReceiver downloadEventReceiver
                = new EpisodeDownloadEventReceiverBuilder().build().setListener(listManager);

        return new PlaylistFragmentProxy(listManager,
                                         playbackEventReceiver,
                                         downloadEventReceiver);
    }
}
