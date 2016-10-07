package net.x4a42.volksempfaenger.data.entity.podcast;

import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.PathProviderBase;

import java.io.File;

public class PodcastPathProvider extends PathProviderBase
{
    public PodcastPathProvider(Context context)
    {
        super(context);
    }

    public File getLogoFile(Podcast podcast)
    {
        return makeDirs(joinPath(context.getExternalFilesDir(null),
                                 "podcastLogos",
                                 String.valueOf(podcast.get_id())));
    }

    public String getLogoUrl(Podcast podcast)
    {
        return makeUrl(getLogoFile(podcast));
    }

}
