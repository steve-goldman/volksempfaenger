package net.x4a42.volksempfaenger.ui.subscriptiongrid.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastPathProvider;

import java.io.File;

public class GridViewHolder
{
    private final View                view;
    private final TextView            titleView;
    private final ImageView           logoView;
    private final PodcastPathProvider podcastPathProvider;
    private final ImageLoader         imageLoader;

    public GridViewHolder(View                view,
                          TextView            titleView,
                          ImageView           logoView,
                          PodcastPathProvider podcastPathProvider,
                          ImageLoader         imageLoader)
    {
        this.view                = view;
        this.titleView           = titleView;
        this.logoView            = logoView;
        this.podcastPathProvider = podcastPathProvider;
        this.imageLoader         = imageLoader;
    }

    public View getView()
    {
        return view;
    }

    public void set(Podcast podcast)
    {
        setTitle(podcast);
        setLogo(podcast);
    }

    private void setTitle(Podcast podcast)
    {
        titleView.setText(podcast.getTitle());
    }

    private void setLogo(Podcast podcast)
    {
        File   logoFile = podcastPathProvider.getLogo(podcast);
        String url      = logoFile.exists() ? logoFile.toURI().toString() : null;
        imageLoader.displayImage(url, logoView);
    }

}
