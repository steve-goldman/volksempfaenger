package net.x4a42.volksempfaenger.ui.playlist;

class PlaylistFragmentProxyBuilder
{
    public PlaylistFragmentProxy build(PlaylistFragment fragment)
    {
        ListManager listManager = new ListManagerBuilder().build(fragment.getActivity());

        return new PlaylistFragmentProxy(fragment,
                                         listManager);
    }
}
