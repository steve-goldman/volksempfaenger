package net.x4a42.volksempfaenger.ui.playlist;

import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiverBuilder;

class PlaylistFragmentProxyBuilder
{
    public PlaylistFragmentProxy build(PlaylistFragment fragment)
    {
        ListManager           listManager = new ListManagerBuilder().build(fragment.getActivity());
        PlaybackEventReceiver receiver    = new PlaybackEventReceiverBuilder().build()
                                                                              .setListener(listManager);

        return new PlaylistFragmentProxy(listManager,
                                         receiver);
    }
}
