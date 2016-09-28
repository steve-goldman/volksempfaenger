package net.x4a42.volksempfaenger.ui.playlist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItem;
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
    private PlaylistItem              playlistItem;

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

    public PlaylistItem getPlaylistItem()
    {
        return playlistItem;
    }

    public void set(PlaylistItem playlistItem)
    {
        this.playlistItem = playlistItem;
        setEpisodeTitle();
        setEpisodeDate();
        setLogo();
    }

    private void setEpisodeTitle()
    {
        episodeTitleView.setText(playlistItem.getEpisode().getTitle());
    }

    private void setEpisodeDate()
    {
        episodeDateView.setText(dateFormatter.format(playlistItem.getEpisode().getPubDate()));
    }

    private void setLogo()
    {
        String url = podcastPathProvider.getLogoUrl(playlistItem.getEpisode().getPodcast());
        logoView.setImageResource(android.R.color.transparent);
        imageLoader.displayImage(url, logoView);
    }
}
