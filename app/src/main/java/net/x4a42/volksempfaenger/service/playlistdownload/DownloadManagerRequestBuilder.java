package net.x4a42.volksempfaenger.service.playlistdownload;

import android.app.DownloadManager;
import android.net.Uri;

class DownloadManagerRequestBuilder
{
    public DownloadManager.Request build(Uri uri)
    {
        return new DownloadManager.Request(uri);
    }
}
