package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.Activity;
import android.content.Intent;

import net.x4a42.volksempfaenger.HtmlConverter;
import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;

class EpisodeSharer
{
    private final Activity      activity;
    private final Episode       episode;
    private final IntentBuilder intentBuilder;
    private final HtmlConverter converter;

    public EpisodeSharer(Activity      activity,
                         Episode       episode,
                         IntentBuilder intentBuilder,
                         HtmlConverter converter)
    {
        this.activity      = activity;
        this.episode       = episode;
        this.intentBuilder = intentBuilder;
        this.converter     = converter;
    }

    public void share()
    {
        // TODO: url shows up as 'null' for some podcasts

        Intent intent = intentBuilder.build(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getSubject());
        intent.putExtra(Intent.EXTRA_TEXT,    getBody());
        activity.startActivity(intent);
    }

    private String getSubject()
    {
        return episode.getPodcast().getTitle() + ": " + episode.getTitle();
    }

    private String getBody()
    {
        return episode.getEpisodeUrl() + "\n\n"
                + getSubject() + "\n\n"
                + converter.toText(episode.getDescription());
    }
}
