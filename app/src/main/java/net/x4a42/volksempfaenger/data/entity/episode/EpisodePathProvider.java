package net.x4a42.volksempfaenger.data.entity.episode;

import android.content.Context;
import android.net.Uri;

import net.x4a42.volksempfaenger.data.entity.PathProviderBase;

import java.io.File;

public class EpisodePathProvider extends PathProviderBase
{
    public EpisodePathProvider(Context context)
    {
        super(context);
    }

    public Uri getEpisodeUri(Episode episode)
    {
        return makeUri(getEpisodeUrl(episode));
    }

    public String getEpisodeUrl(Episode episode)
    {
        return makeUrl(getEpisodeFile(episode));
    }

    private File getEpisodeFile(Episode episode)
    {
        return makeDirs(joinPath(context.getExternalFilesDir(null),
                                 "episodes",
                                 String.valueOf(episode.getPodcastId()),
                                 String.valueOf(episode.get_id())));
    }
}
