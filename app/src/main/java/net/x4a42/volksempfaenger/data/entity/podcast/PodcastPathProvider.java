package net.x4a42.volksempfaenger.data.entity.podcast;

import android.content.Context;

import java.io.File;

public class PodcastPathProvider
{
    private final Context context;

    public PodcastPathProvider(Context context)
    {
        this.context = context;
    }

    public File getLogo(Podcast podcast)
    {
        return makeDirs(joinPath(context.getExternalFilesDir(null),
                                 "podcastLogos",
                                 String.valueOf(podcast.get_id())));
    }

    private File makeDirs(File file)
    {
        File parent = file.getParentFile();
        //noinspection ResultOfMethodCallIgnored
        parent.mkdirs();
        return file;
    }

    private File joinPath(File base, String... children)
    {
        for (String child : children)
        {
            base = new File(base, child);
        }
        return base;
    }
}
