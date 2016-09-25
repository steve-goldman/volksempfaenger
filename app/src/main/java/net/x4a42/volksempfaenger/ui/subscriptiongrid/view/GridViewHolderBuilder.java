package net.x4a42.volksempfaenger.ui.subscriptiongrid.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastPathProvider;

class GridViewHolderBuilder
{
    public GridViewHolder build(View view)
    {
        PodcastPathProvider podcastPathProvider = new PodcastPathProvider(view.getContext());

        return new GridViewHolder(view,
                                  (TextView) view.findViewById(R.id.podcast_title),
                                  (ImageView) view.findViewById(R.id.podcast_logo),
                                  podcastPathProvider,
                                  ImageLoader.getInstance());
    }
}
