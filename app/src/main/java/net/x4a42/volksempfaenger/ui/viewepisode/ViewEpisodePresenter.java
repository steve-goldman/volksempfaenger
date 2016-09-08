package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.Activity;
import android.text.Spanned;
import android.widget.TextView;

import net.x4a42.volksempfaenger.HtmlConverter;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.EpisodeCursor;

class ViewEpisodePresenter
{
    private static final long   MegaByte = 1024 * 1024;
    private final Activity      activity;
    private final HtmlConverter converter;
    private TextView            title;
    private TextView            meta;
    private TextView            description;

    public ViewEpisodePresenter(Activity      activity,
                                HtmlConverter converter)
    {
        this.activity  = activity;
        this.converter = converter;
    }

    public void onCreate()
    {
        activity.setContentView(R.layout.view_episode);
        this.title       = (TextView) activity.findViewById(R.id.episode_title);
        this.meta        = (TextView) activity.findViewById(R.id.episode_meta);
        this.description = (TextView) activity.findViewById(R.id.episode_description);
    }

    public void update(EpisodeCursor cursor)
    {
        activity.setTitle(cursor.getTitle());
        updateHeader(cursor);
        updateDescription(cursor);
    }

    private void updateHeader(EpisodeCursor cursor)
    {
        title.setText(cursor.getTitle());
        meta.setText(getMetaString(cursor));
    }

    private void updateDescription(EpisodeCursor cursor)
    {
        String description = cursor.getDescription();
        if (description == null)
        {
            this.description.setText("");
            return;
        }

        // TODO: get this static call out of here
        Spanned spanned = converter.toSpanned(description);
        // TODO: download images if any
        this.description.setText(spanned);
    }

    private String getMetaString(EpisodeCursor cursor)
    {
        return getDurationString(cursor) + "  " + getDownloadedString(cursor);
    }

    private String getDurationString(EpisodeCursor cursor)
    {
        int duration = cursor.getDurationTotal();
        return duration > 0 ? Utils.formatTime(duration) : "unknown duration";
    }

    private String getDownloadedString(EpisodeCursor cursor)
    {
        long size = cursor.getDownloadTotal();
        if (size <= 0)
        {
            size = cursor.getEnclosureSize();
        }

        return size > 0 ? String.format("%.2f MiB", ((double) size) / MegaByte) : "unknown size";
    }
}
