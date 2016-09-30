package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.Activity;

import net.x4a42.volksempfaenger.HtmlConverter;
import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;

class EpisodeSharerBuilder
{
    public EpisodeSharer build(Activity activity, Episode episode)
    {
        IntentBuilder intentBuilder = new IntentBuilder();
        HtmlConverter converter     = new HtmlConverter();
        return new EpisodeSharer(activity, episode, intentBuilder, converter);
    }
}
