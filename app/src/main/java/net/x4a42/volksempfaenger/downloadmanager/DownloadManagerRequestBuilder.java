package net.x4a42.volksempfaenger.downloadmanager;

import android.app.DownloadManager;
import android.net.Uri;

class DownloadManagerRequestBuilder
{
    public DownloadManager.Request build(Uri uri)
    {
        return new DownloadManager.Request(uri);
    }
}
