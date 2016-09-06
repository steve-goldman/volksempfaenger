package net.x4a42.volksempfaenger.service.playback;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;

import net.x4a42.volksempfaenger.data.Columns;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;

import java.io.File;

public class PlaybackItemBuilder
{
    private static final String[] EpisodeFields = new String[]
            {
                    Columns.Episode._ID,
                    Columns.Episode.TITLE,
                    Columns.Episode.STATUS,
                    Columns.Episode.PODCAST_ID,
                    Columns.Episode.ENCLOSURE_ID,
                    Columns.Episode.DOWNLOAD_ID,
                    Columns.Episode.DOWNLOAD_LOCAL_URI,
                    Columns.Episode.DURATION_LISTENED,
                    Columns.Episode.PODCAST_TITLE,
                    Columns.Episode.ENCLOSURE_URL,
                    Columns.Episode.FLATTR_STATUS
            };

    public PlaybackItem build(Context context, Uri episodeUri)
    {
        EpisodeCursor cursor = buildEpisodeCursor(context, episodeUri);

        return new PlaybackItem(episodeUri,
                                buildUriTime(episodeUri),
                                cursor.getPodcastUri(),
                                buildPath(cursor),
                                cursor.getTitle(),
                                cursor.getPodcastTitle(),
                                cursor.getDurationListened(),
                                cursor.getPodcastId());
    }

    private EpisodeCursor buildEpisodeCursor(Context context, Uri episodeUri)
    {
        EpisodeCursor cursor = new EpisodeCursor(
                context.getContentResolver().query(episodeUri, EpisodeFields, null, null, null));

        if (!cursor.moveToFirst())
        {
            throw new IllegalArgumentException("unable to find episode");
        }

        return cursor;
    }

    private Uri buildUriTime(Uri episodeUri)
    {
        return ContentUris.withAppendedId(VolksempfaengerContentProvider.EPISODETIME_URI,
                                          ContentUris.parseId(episodeUri));
    }

    private String buildPath(EpisodeCursor cursor)
    {
        File enclosurFile = cursor.getDownloadFile();
        if (enclosurFile == null || !enclosurFile.isFile())
        {
            return cursor.getEnclosureUrl();
        }
        else
        {
            return enclosurFile.getAbsolutePath();
        }
    }
}
