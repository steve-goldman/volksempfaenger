package net.x4a42.volksempfaenger.data.episode;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import net.x4a42.volksempfaenger.data.Columns;
import net.x4a42.volksempfaenger.data.EpisodeCursor;

public class EpisodeCursorLoader implements LoaderManager.LoaderCallbacks<Cursor>
{
    public interface Listener
    {
        void onCursorLoaded(EpisodeCursor episodeCursor);
        void onCursorReset();
    }

    private static final int    LoaderId = 0;
    private final Context       context;
    private final LoaderManager loaderManager;
    private final Uri           episodeUri;
    private Listener            listener;

    public EpisodeCursorLoader(Context       context,
                               LoaderManager loaderManager,
                               Uri           episodeUri)
    {
        this.context       = context;
        this.loaderManager = loaderManager;
        this.episodeUri    = episodeUri;
    }

    public EpisodeCursorLoader setListener(Listener listener)
    {
        this.listener = listener;
        return this;
    }

    public void init()
    {
        loaderManager.restartLoader(LoaderId, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        String[] projection =
                {
                        Columns.Episode._ID,
                        Columns.Episode.TITLE,
                        Columns.Episode.DESCRIPTION,
                        Columns.Episode.STATUS,
                        Columns.Episode.DATE,
                        Columns.Episode.DURATION_TOTAL,
                        Columns.Episode.DURATION_LISTENED,
                        Columns.Episode.URL,
                        Columns.Episode.PODCAST_ID,
                        Columns.Episode.PODCAST_TITLE,
                        Columns.Episode.DOWNLOAD_ID,
                        Columns.Episode.DOWNLOAD_BYTES_DOWNLOADED_SO_FAR,
                        Columns.Episode.DOWNLOAD_LOCAL_URI,
                        Columns.Episode.DOWNLOAD_STATUS,
                        Columns.Episode.DOWNLOAD_TOTAL_SIZE_BYTES,
                        Columns.Episode.ENCLOSURE_ID,
                        Columns.Episode.ENCLOSURE_SIZE,
                        Columns.Episode.FLATTR_STATUS,
                        Columns.Episode.FLATTR_URL,
                        Columns.Episode.ENCLOSURE_NUMBER
                };

        return new CursorLoader(context, episodeUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        EpisodeCursor episodeCursor = new EpisodeCursor(cursor);
        episodeCursor.moveToFirst();
        listener.onCursorLoaded(new EpisodeCursor(cursor));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        listener.onCursorReset();
    }
}
