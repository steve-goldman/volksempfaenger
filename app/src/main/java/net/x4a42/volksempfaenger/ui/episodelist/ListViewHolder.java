package net.x4a42.volksempfaenger.ui.episodelist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastPathProvider;
import net.x4a42.volksempfaenger.misc.DateFormatter;

class ListViewHolder
{
    private final View                view;
    private final TextView            episodeTitleView;
    private final TextView            episodeDateView;
    private final ImageView           logoView;
    private final DateFormatter       dateFormatter;
    private final PodcastPathProvider podcastPathProvider;
    private final ImageLoader         imageLoader;
    private Episode                   episode;

    public ListViewHolder(View                view,
                          TextView            episodeTitleView,
                          TextView            episodeDateView,
                          ImageView           logoView,
                          DateFormatter       dateFormatter,
                          PodcastPathProvider podcastPathProvider,
                          ImageLoader         imageLoader)
    {
        this.view                = view;
        this.episodeTitleView    = episodeTitleView;
        this.episodeDateView     = episodeDateView;
        this.logoView            = logoView;
        this.dateFormatter       = dateFormatter;
        this.podcastPathProvider = podcastPathProvider;
        this.imageLoader         = imageLoader;
    }

    public View getView()
    {
        return view;
    }

    public Episode getEpisode()
    {
        return episode;
    }

    public void set(Episode episode)
    {
        if (this.episode != null && this.episode.get_id() == episode.get_id())
        {
            return;
        }
        this.episode = episode;

        setEpisodeTitle();
        setEpisodeDate();
        setLogo();
    }

    private void setEpisodeTitle()
    {
        episodeTitleView.setText(episode.getTitle());
    }

    private void setEpisodeDate()
    {
        episodeDateView.setText(dateFormatter.format(episode.getPubDate()));
    }

    private void setLogo()
    {
        String url = podcastPathProvider.getLogoUrl(episode.getPodcast());
        logoView.setImageResource(android.R.color.transparent);
        imageLoader.displayImage(url, logoView);
    }
}
