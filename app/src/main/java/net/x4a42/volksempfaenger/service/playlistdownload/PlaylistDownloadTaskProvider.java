package net.x4a42.volksempfaenger.service.playlistdownload;

import android.content.Context;

class PlaylistDownloadTaskProvider
{
    private final Context                          context;
    private final PlaylistDownloadTaskProxyBuilder taskProxyBuilder;

    public PlaylistDownloadTaskProvider(Context                          context,
                                        PlaylistDownloadTaskProxyBuilder taskProxyBuilder)
    {
        this.context          = context;
        this.taskProxyBuilder = taskProxyBuilder;
    }

    public PlaylistDownloadTask get()
    {
        PlaylistDownloadTaskProxy proxy = taskProxyBuilder.build();
        return new PlaylistDownloadTask(proxy);
    }
}
