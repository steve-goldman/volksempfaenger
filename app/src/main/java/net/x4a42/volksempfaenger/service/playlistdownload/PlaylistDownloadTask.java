package net.x4a42.volksempfaenger.service.playlistdownload;

import android.os.AsyncTask;

class PlaylistDownloadTask extends AsyncTask<Void, Void, Void>
{
    private final PlaylistDownloadTaskProxy playlistDownloadTaskProxy;

    public PlaylistDownloadTask(PlaylistDownloadTaskProxy playlistDownloadTaskProxy)
    {
        this.playlistDownloadTaskProxy = playlistDownloadTaskProxy;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        playlistDownloadTaskProxy.doInBackground();
        return null;
    }
}
