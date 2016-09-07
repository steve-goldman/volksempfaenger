package net.x4a42.volksempfaenger.data.episode;

import android.app.Activity;
import android.app.LoaderManager;
import android.net.Uri;

public class EpisodeCursorLoaderBuilder
{
    public EpisodeCursorLoader build(Activity activity, Uri episodeUri)
    {
        LoaderManager loaderManager = activity.getLoaderManager();
        return new EpisodeCursorLoader(activity, loaderManager, episodeUri);
    }
}
