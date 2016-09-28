package net.x4a42.volksempfaenger.ui.playlist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastPathProvider;
import net.x4a42.volksempfaenger.misc.DateFormatter;
import net.x4a42.volksempfaenger.misc.ImageLoaderProvider;

class ListViewHolderBuilder
{
    public ListViewHolder build(View view)
    {
        PodcastPathProvider podcastPathProvider = new PodcastPathProvider(view.getContext());
        ImageLoader imageLoader                 = new ImageLoaderProvider(view.getContext()).get();

        return new ListViewHolder(view,
                                  (TextView)  view.findViewById(R.id.episode_title),
                                  (TextView)  view.findViewById(R.id.episode_date),
                                  (ImageView) view.findViewById(R.id.podcast_logo),
                                  new DateFormatter(),
                                  podcastPathProvider,
                                  imageLoader);
    }
}
