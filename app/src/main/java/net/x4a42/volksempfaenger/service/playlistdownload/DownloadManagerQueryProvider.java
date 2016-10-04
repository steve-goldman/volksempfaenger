package net.x4a42.volksempfaenger.service.playlistdownload;

import android.app.DownloadManager;

class DownloadManagerQueryProvider
{
    public DownloadManager.Query get()
    {
        return new DownloadManager.Query();
    }
}
