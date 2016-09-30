package net.x4a42.volksempfaenger.ui.playlist;

import android.content.Context;

import net.x4a42.volksempfaenger.data.playlist.Playlist;
import net.x4a42.volksempfaenger.data.playlist.PlaylistProvider;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProviderBuilder;

class ListManagerBuilder
{
    public ListManager build(Context context)
    {
        ListAdapterProxy listAdapterProxy
                = new ListAdapterProxyBuilder().build(context);

        ViewEpisodeActivityIntentProvider intentProvider
                = new ViewEpisodeActivityIntentProviderBuilder().build(context);

        Playlist               playlist        = new PlaylistProvider(context).get();

        return new ListManager(context,
                               listAdapterProxy,
                               intentProvider,
                               playlist);
    }
}
