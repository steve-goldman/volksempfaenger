package net.x4a42.volksempfaenger.data.playlist;

import android.content.Context;

public class PlaylistProvider
{
    private static Playlist instance;
    private final Context   context;

    public PlaylistProvider(Context context)
    {
        this.context = context;
    }

    public Playlist get()
    {
        if (instance == null)
        {
            instance = new PlaylistBuilder().build(context);
        }

        return instance;
    }
}
