package net.x4a42.volksempfaenger.service.playlistdownload;

import android.app.Service;

class PlaylistDownloadServiceProxyBuilder
{
    public PlaylistDownloadServiceProxy build(Service service)
    {
        IntentParser intentParser = new IntentParser();

        PlaylistDownloadTaskProxyBuilder taskProxyBuilder
                = new PlaylistDownloadTaskProxyBuilder(service);

        PlaylistDownloadTaskProvider taskProvider
                = new PlaylistDownloadTaskProvider(service, taskProxyBuilder);

        PlaylistDownloadServiceProxy proxy
                = new PlaylistDownloadServiceProxy(intentParser,
                                                   taskProvider);

        intentParser.setListener(proxy);

        return proxy;
    }
}
