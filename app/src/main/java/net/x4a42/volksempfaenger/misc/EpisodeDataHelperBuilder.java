package net.x4a42.volksempfaenger.misc;

import android.content.Context;

public class EpisodeDataHelperBuilder
{
    public EpisodeDataHelper build(Context context)
    {
        return new EpisodeDataHelper(context.getContentResolver(), new ContentValuesFactory());
    }
}
