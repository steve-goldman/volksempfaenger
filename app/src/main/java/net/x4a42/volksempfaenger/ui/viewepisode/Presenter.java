package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.Activity;
import android.text.Spanned;
import android.widget.TextView;

import net.x4a42.volksempfaenger.HtmlConverter;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;

class Presenter
{
    private static final long   MegaByte = 1024 * 1024;
    private final Activity      activity;
    private final Episode       episode;
    private final HtmlConverter converter;

    public Presenter(Activity      activity,
                     Episode       episode,
                     HtmlConverter converter)
    {
        this.activity  = activity;
        this.episode   = episode;
        this.converter = converter;
    }

    public void onCreate()
    {
        activity.setTitle(episode.getTitle());
        ((TextView) activity.findViewById(R.id.episode_title)).setText(episode.getTitle());
        ((TextView) activity.findViewById(R.id.episode_meta)).setText(getMetaString());
        updateDescription((TextView) activity.findViewById(R.id.episode_description));
    }

    private void updateDescription(TextView description)
    {
        if (episode.getDescription() == null)
        {
            description.setText("");
            return;
        }

        Spanned spanned = converter.toSpanned(episode.getDescription());
        // TODO: download images if any
        description.setText(spanned);
    }

    private String getMetaString()
    {
        return getDurationString() + "  " + getDownloadedString();
    }

    private String getDurationString()
    {
        return episode.getDuration() > 0 ? Utils.formatTimeFriendly((int) episode.getDuration()) : "unknown duration";
    }

    private String getDownloadedString()
    {
        long size = episode.getEnclosures().get(0).getSize();
        return size > 0 ? String.format("%.2f MiB", ((double) size) / MegaByte) : "unknown size";
    }
}
