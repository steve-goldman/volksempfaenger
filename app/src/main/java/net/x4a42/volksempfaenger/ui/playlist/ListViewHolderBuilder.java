package net.x4a42.volksempfaenger.ui.playlist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastPathProvider;
import net.x4a42.volksempfaenger.misc.DateFormatter;
import net.x4a42.volksempfaenger.misc.ImageLoaderProvider;

class ListViewHolderBuilder
{
    public ListViewHolder build(View view)
    {
        PodcastPathProvider       podcastPathProvider = new PodcastPathProvider(view.getContext());
        ImageLoader               imageLoader         = new ImageLoaderProvider(view.getContext()).get();
        EpisodeDownloadDaoWrapper episodeDownloadDao  = new EpisodeDownloadDaoBuilder().build(view.getContext());

        return new ListViewHolder(view,
                                  (TextView)  view.findViewById(R.id.episode_title),
                                  (TextView)  view.findViewById(R.id.episode_date),
                                  (ImageView) view.findViewById(R.id.podcast_logo),
                                  (ImageView) view.findViewById(R.id.badge),
                                  new DateFormatter(),
                                  podcastPathProvider,
                                  imageLoader,
                                  episodeDownloadDao);
    }
}
