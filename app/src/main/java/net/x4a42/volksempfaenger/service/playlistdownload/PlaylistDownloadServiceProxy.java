package net.x4a42.volksempfaenger.service.playlistdownload;

import android.app.Service;
import android.content.Intent;

class PlaylistDownloadServiceProxy implements IntentParser.Listener
{
    private final IntentParser                 intentParser;
    private final PlaylistDownloadTaskProvider playlistDownloadTaskProvider;

    public PlaylistDownloadServiceProxy(IntentParser                 intentParser,
                                        PlaylistDownloadTaskProvider playlistDownloadTaskProvider)
    {
        this.intentParser = intentParser;
        this.playlistDownloadTaskProvider = playlistDownloadTaskProvider;
    }

    public void onCreate()
    {

    }

    public void onDestroy()
    {

    }

    public int onStartCommand(Intent intent)
    {
        intentParser.parse(intent);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onRun()
    {
        // TODO: don't run this if a task in queue is waiting to begin

        // these tasks must NOT run in parallel
        playlistDownloadTaskProvider.get().execute();
    }
}
