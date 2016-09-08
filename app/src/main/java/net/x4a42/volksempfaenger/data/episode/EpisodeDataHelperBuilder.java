package net.x4a42.volksempfaenger.data.episode;

import android.app.DownloadManager;
import android.content.Context;

import net.x4a42.volksempfaenger.misc.ContentValuesFactory;

public class EpisodeDataHelperBuilder
{
    public EpisodeDataHelper build(Context context)
    {
        return new EpisodeDataHelper(
                context.getContentResolver(),
                new ContentValuesFactory(),
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE));
    }
}
