package net.x4a42.volksempfaenger.ui.playlist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItem;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastPathProvider;
import net.x4a42.volksempfaenger.misc.DateFormatter;

class ListViewHolder
{
    private final View                      view;
    private final TextView                  episodeTitleView;
    private final TextView                  episodeDateView;
    private final ImageView                 logoView;
    private final ImageView                 badgeView;
    private final DateFormatter             dateFormatter;
    private final PodcastPathProvider       podcastPathProvider;
    private final ImageLoader               imageLoader;
    private final EpisodeDownloadDaoWrapper episodeDownloadDao;
    private PlaylistItem                    playlistItem;

    public ListViewHolder(View                      view,
                          TextView                  episodeTitleView,
                          TextView                  episodeDateView,
                          ImageView                 logoView,
                          ImageView                 badgeView,
                          DateFormatter             dateFormatter,
                          PodcastPathProvider       podcastPathProvider,
                          ImageLoader               imageLoader,
                          EpisodeDownloadDaoWrapper episodeDownloadDao)
    {
        this.view                = view;
        this.episodeTitleView    = episodeTitleView;
        this.episodeDateView     = episodeDateView;
        this.logoView            = logoView;
        this.badgeView           = badgeView;
        this.dateFormatter       = dateFormatter;
        this.podcastPathProvider = podcastPathProvider;
        this.imageLoader         = imageLoader;
        this.episodeDownloadDao  = episodeDownloadDao;
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
        if (this.playlistItem != null && this.playlistItem.get_id().equals(playlistItem.get_id()))
        {
            setBadge();
            return;
        }
        this.playlistItem = playlistItem;

        setEpisodeTitle();
        setEpisodeDate();
        setLogo();
        setBadge();
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

    private void setBadge()
    {
        if (episodeDownloadDao.hasSuccessfulDownload(playlistItem.getEpisode()))
        {
            badgeView.setVisibility(View.VISIBLE);
        }
        else
        {
            badgeView.setVisibility(View.INVISIBLE);
        }
    }
}
