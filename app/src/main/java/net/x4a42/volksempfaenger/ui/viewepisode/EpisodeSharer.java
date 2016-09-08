package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.Activity;
import android.content.Intent;

import net.x4a42.volksempfaenger.HtmlConverter;
import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.data.EpisodeCursor;

class EpisodeSharer
{
    private final Activity      activity;
    private final IntentBuilder intentBuilder;
    private final HtmlConverter converter;

    public EpisodeSharer(Activity      activity,
                         IntentBuilder intentBuilder,
                         HtmlConverter converter)
    {
        this.activity      = activity;
        this.intentBuilder = intentBuilder;
        this.converter     = converter;
    }

    public void share(EpisodeCursor cursor)
    {
        // TODO: url shows up as 'null' for some podcasts

        Intent intent = intentBuilder.build(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getSubject(cursor));
        intent.putExtra(Intent.EXTRA_TEXT,    getBody(cursor));
        activity.startActivity(intent);
    }

    private String getSubject(EpisodeCursor cursor)
    {
        return cursor.getPodcastTitle() + ": " + cursor.getTitle();
    }

    private String getBody(EpisodeCursor cursor)
    {
        return cursor.getUrl() + "\n\n"
                + getSubject(cursor) + "\n\n"
                + converter.toText(cursor.getDescription());
    }
}
