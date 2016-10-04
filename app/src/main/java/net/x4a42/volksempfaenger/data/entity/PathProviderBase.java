package net.x4a42.volksempfaenger.data.entity;

import android.content.Context;
import android.net.Uri;

import java.io.File;

public class PathProviderBase
{
    protected final Context context;

    protected PathProviderBase(Context context)
    {
        this.context = context;
    }

    protected File makeDirs(File file)
    {
        File parent = file.getParentFile();
        //noinspection ResultOfMethodCallIgnored
        parent.mkdirs();
        return file;
    }

    protected File joinPath(File base, String... children)
    {
        for (String child : children)
        {
            base = new File(base, child);
        }
        return base;
    }

    protected String makeUrl(File file)
    {
        return "file:///" + file.getAbsolutePath();
    }

    protected Uri makeUri(String url)
    {
        return Uri.parse(url);
    }
}
